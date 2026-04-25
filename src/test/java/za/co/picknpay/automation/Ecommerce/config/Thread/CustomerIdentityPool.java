package za.co.picknpay.automation.Ecommerce.config.Thread;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manages a thread-safe pool of test identities.
 * This component prevents parallel test execution threads from colliding by
 * "leasing" customers to scenarios and reclaiming them once the test finishes.
 */
@Component
public class CustomerIdentityPool {

    /** * availableEmails: A thread-safe queue that blocks when empty.
     * This acts as the "semaphore" for the pool; if it's empty, threads will wait.
     */
    private final LinkedBlockingQueue<String> availableEmails;

    /** * customerPoolData: A master map holding the current state (tokens, profiles)
     * of every customer loaded. ConcurrentHashMap prevents race conditions during updates.
     */
    private final Map<String, Customer> customerPoolData;

    @Autowired
    public CustomerIdentityPool(CustomerProperties customerProperties) {



        // Initialize the master data map with filtered customers
        this.customerPoolData = new ConcurrentHashMap<>(
                customerProperties.getCustomers().stream()
                        // Ensure we only load Business Profiles for OBP or Transpharm accounts for Transpharm

                        // Use the original email as the unique key for the pool
                        .collect(Collectors.toMap(Customer::getOriginalEmail, Function.identity()))
        );

        // Fill the queue with the IDs (emails). These represent the "keys" available to be checked out.
        this.availableEmails = new LinkedBlockingQueue<>(this.customerPoolData.keySet());
        System.out.println("CustomerIdentityPool initialized with " + availableEmails.size() + " customers.");
    }

    /**
     * Leases an email address to a calling thread.
     * * @return A unique customer email from the pool.
     * @throws RuntimeException if no emails become available within the 60s timeout.
     */
    public String acquireEmail() throws InterruptedException {
        System.out.println("Acquiring email... Available in pool: " + availableEmails.size());

        // .poll() waits for an item to appear. If a test finishes and releases an email,
        // this waiting thread will instantly grab it and continue.
        String email = availableEmails.poll(60, TimeUnit.SECONDS);

        if (email == null) {
            throw new RuntimeException("CRITICAL: Pool Exhausted. No customers available after 60s. " +
                    "Consider increasing customer count in config or reducing parallel thread count.");
        }
        return email;
    }

    /**
     * Returns a customer to the pool and persists session state.
     * * @param customerToRelease The modified customer object from the finished test.
     */
    public void releaseCustomer(Customer customerToRelease) throws InterruptedException {
        if (customerToRelease == null || customerToRelease.getOriginalEmail() == null) {
            return;
        }

        String email = customerToRelease.getOriginalEmail();

        // PERSIST STATE: Update the master pool with the AccessToken/Session data.
        // This allows the next test using this email to potentially bypass Login UI steps.
        customerPoolData.computeIfPresent(email, (k, pooledCustomer) -> {


            return pooledCustomer;
        });

        // Add the email back to the queue to signal availability to waiting threads.
        if (!availableEmails.contains(email)) {
            availableEmails.put(email);
            System.out.println("Released: " + email + ". Total Available: " + availableEmails.size());
        }
    }

    /**
     * Fetches a decoupled "DTO" copy of the customer data.
     * * @param email The key acquired via acquireEmail().
     * @return A new instance of Customer (Prototype Pattern) to ensure thread isolation.
     */
    public Customer getCustomerDataByEmail(String email) {
        Customer pooledCustomer = customerPoolData.get(email);
        if (pooledCustomer == null) {
            throw new IllegalArgumentException("Customer not found in pool: " + email);
        }

        // Return a NEW object. If the test thread modifies this object, it won't
        // affect other threads until releaseCustomer() is explicitly called.
        return Customer.builder()
                .email(pooledCustomer.getEmail()).
                originalEmail(pooledCustomer.getOriginalEmail())
                .emailSecretKey(pooledCustomer.getEmailSecretKey())
                .isRegistered(pooledCustomer.isRegistered())
                .name(pooledCustomer.getName())
                .lastName(pooledCustomer.getLastName())
                .password(pooledCustomer.getPassword())
                .build();


    }
}
