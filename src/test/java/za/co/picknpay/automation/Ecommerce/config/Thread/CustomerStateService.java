package za.co.picknpay.automation.Ecommerce.config.Thread;


import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Service;


/**
 * Service responsible for managing the state of the customer currently assigned to a scenario.
 * * @ScenarioScope is the "Magic Sauce" here: It guarantees that each parallel test execution
 * has its own unique instance of this service. This prevents Thread A (Scenario: Login)
 * from accidentally overwriting the AccessToken of Thread B (Scenario: Checkout).
 */
@Service
@ScenarioScope
public class CustomerStateService {

    /**
     * The active Customer object for the current scenario.
     * This holds the identity, credentials, and transient session data (like tokens).
     */
    private Customer currentCustomer;

    /**
     * Retrieves the full customer context for the current scenario.
     * @return The active Customer object assigned to this specific thread.
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Assigns a customer to the current scenario.
     * This is typically called in a @Before hook or a 'Given' step after
     * acquiring an email from the CustomerIdentityPool.
     */
    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    /**
     * Manually resets the state.
     * While @ScenarioScope destroys this bean at the end of every scenario,
     * this method is useful if a single test needs to "log out" and "log in"
     * as a different user within the same flow.
     */
    public void clear() {
        this.currentCustomer = null;
    }








}
