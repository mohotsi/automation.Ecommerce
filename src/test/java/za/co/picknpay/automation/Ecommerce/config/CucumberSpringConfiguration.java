package za.co.picknpay.automation.Ecommerce.config;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;
import za.co.picknpay.automation.Ecommerce.config.Thread.CustomerIdentityPool;

/**
 * Configuration class for integrating Cucumber with Spring and Playwright.
 * This class handles the setup of the Playwright engine and the
 * per-scenario customer data lifecycle.
 */
@Configuration
public class CucumberSpringConfiguration {

    /**
     * Injected pool containing test user identities.
     * Manages thread-safe email acquisition for parallel execution.
     */
    @Autowired
    private CustomerIdentityPool customerIdentityPool;

    /**
     * Initializes the Playwright engine instance.
     * * LIFECYCLE NOTE:
     * Currently using the default Singleton scope. Playwright is heavyweight;
     * creating it once per test suite execution is more performant than
     * creating it per scenario.
     * * @return A thread-safe Playwright instance.
     */
    @Bean(destroyMethod = "close") // Ensures browser processes are killed on JVM shutdown
    public Playwright playwright() {
        // Log the creation to help debug lifecycle issues in the console
        System.out.println("Creating Playwright bean (Scope: Singleton/Default)");
        return Playwright.create();
    }

    /**
     * Factory bean to create and configure a Customer object for every individual Cucumber scenario.
     * * SCENARIO SCOPE:
     * This bean is destroyed and recreated for every "Scenario" in your feature files.
     * This ensures test isolation so that data from one test doesn't leak into another.
     *
     * @param apiRequestContext Injected via PlayWrightAPI config - used for REST calls.
     * @param page              Injected via PlayWrightAPI config - used for UI interaction.
     * @return A fully initialized Customer object ready for the test steps.
     * @throws InterruptedException if the thread is interrupted while waiting for an available email.
     */
    @Bean
    @ScenarioScope
    public Customer scenarioCustomer(
            @Qualifier("customerAPIRequestContext") APIRequestContext apiRequestContext,
            Page page) throws InterruptedException {

        // 1. Claim a unique email from the shared pool to prevent race conditions in parallel tests
        String acquiredEmail = customerIdentityPool.acquireEmail();

        // 2. Map the email to a data object (fetching passwords, names, etc., from JSON/DB)
        Customer customer = customerIdentityPool.getCustomerDataByEmail(acquiredEmail);

        // 3. Inject the Playwright communication channels into the Customer object
        // This allows the 'Customer' to perform its own actions (e.g., customer.login())
//        customer.setApiRequestContext(apiRequestContext);
//        customer.setPage(page);
//
//        // 4. Reset state flags to ensure a clean slate for the new scenario
//        customer.setHasAlreadyFetchData(false);
        customer.setOriginalEmail(acquiredEmail);

        // 5. Diagnostic logging for visibility into the Cucumber-Spring dependency injection
        System.out.println("--- Scenario Context Initialized ---");
        System.out.println("User: " + customer.getEmail());
        System.out.println("Page Object ID: " + (page != null ? page.hashCode() : "null"));
        System.out.println("API Context ID: " + (apiRequestContext != null ? apiRequestContext.hashCode() : "null"));
        System.out.println("------------------------------------");

        return customer;
    }
}