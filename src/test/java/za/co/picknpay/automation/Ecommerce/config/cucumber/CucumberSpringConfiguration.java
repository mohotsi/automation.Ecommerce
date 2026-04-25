package za.co.picknpay.automation.Ecommerce.config.cucumber;



import com.microsoft.playwright.Playwright;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;
import za.co.picknpay.automation.Ecommerce.config.Thread.CustomerIdentityPool;

/**
 * Spring Configuration class that orchestrates the lifecycle of Playwright and Test Identities.
 * This class ensures that every Cucumber Scenario gets its own isolated environment.
 */
@Configuration
public class CucumberSpringConfiguration {

    /**
     * The Shared Identity Pool. Unlike the other beans here, this is a SINGLETON.
     * It must be shared across all threads to act as the traffic controller for user accounts.
     */
    @Autowired
    private CustomerIdentityPool customerIdentityPool;

    /**
     * Initializes the Playwright core engine.
     * * ARCHITECTURE NOTE: By default, this is a Singleton. While Playwright is heavyweight,
     * it is thread-safe. Creating one per application is more efficient than creating one
     * per scenario, as it reduces overhead during test startup.
     * * @return The root Playwright instance used to spawn Browsers and Contexts.
     */
    @Bean(destroyMethod = "close") // Ensures clean process termination on app shutdown
    public Playwright playwright() {
        System.out.println("[CORE] Creating Singleton Playwright engine instance.");
        return Playwright.create();
    }

    /**
     * The "Identity Factory" for scenarios.
     * This bean is the critical link between a thread-safe pool and a specific test execution.
     *
     * @ScenarioScope: This is vital. It tells Spring: "Run this method once per scenario."
     * When the scenario ends, Spring discards this object.
     *

     * @return A 'Customer' object that carries all necessary tools for the current test.
     * @throws InterruptedException if the thread is waiting for an email and is interrupted.
     */
    @Bean
    @ScenarioScope
    public Customer scenarioCustomer(
            ) throws InterruptedException {

        // 1. LEASE: Request an available user from the pool.
        // If the pool is empty, this thread will block (pause) here for up to 60s.
        String acquiredEmail = customerIdentityPool.acquireEmail();

        // 2. INITIALIZE: Get a fresh DTO (copy) of the customer data.
        Customer customer = customerIdentityPool.getCustomerDataByEmail(acquiredEmail);

        // 3. WIRE-UP: Inject the scenario-specific Playwright objects into the customer.
        // This allows the Customer object to perform both UI (page) and API actions.


        // 4. RESET STATE: Ensure a clean slate for the current scenario.

        customer.setOriginalEmail(acquiredEmail);



        return customer;
    }
}
