package za.co.picknpay.automation.Ecommerce.definition;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import za.co.picknpay.automation.Ecommerce.Notifications.model.Email;
import za.co.picknpay.automation.Ecommerce.Page.CartPage;
import za.co.picknpay.automation.Ecommerce.Page.CheckoutPage;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;
import za.co.picknpay.automation.Ecommerce.service.OMSTaskAutomation;
import za.co.picknpay.automation.Ecommerce.service.OrderAutomationService;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;
import static za.co.picknpay.automation.Ecommerce.Util.Try;

public class CheckoutStepDefinitions {

    String uiOrderId;
    @Autowired
    private CartPage cartPage;

    @Autowired
    private CheckoutPage checkoutPage;
    @Autowired
    Page page;
    @Autowired
    OrderAutomationService orderAutomationService;
    @Autowired
    private OMSTaskAutomation taskAutomation;
    @Autowired
    Email email;

    @Autowired
    Customer customer;
    @And("they place the order to reach the payment page")
    public void placeOrderSummary() {
        checkoutPage.clickPlaceOrder();
    }

    @And("they submit the final payment details with the following card info:")
    public void submitPaymentDetails(io.cucumber.datatable.DataTable table) {
        Map<String, String> cardData = table.asMap(String.class, String.class);

        checkoutPage.fillPaymentForm(
                cardData.get("Name on Card"),
                cardData.get("Card Number"),
                cardData.get("CVC"),
                cardData.get("Expiration Month"),
                cardData.get("Expiration Year")
        );

        checkoutPage.clickPayAndConfirm();
    }
    @Then("the order should not be placed or confirmed")
    public void verifyOrderIsNotPlaced() {
        // 1. Assert the Success Header is NOT visible
        // Ensures the "ORDER PLACED!" message did not appear.
        assertThat(page.getByText("Order Placed!", new Page.GetByTextOptions().setExact(true)))
                .isHidden();

        // 2. Assert the Confirmation Message is NOT visible
        assertThat(page.getByText("Congratulations! Your order has been confirmed!"))
                .isHidden();

        // 3. Assert functional elements are NOT present
        // If the order failed, the Invoice download and Continue links shouldn't exist.
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Download Invoice")))
                .isHidden();

        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")))
                .isHidden();

        // 4. Traceability Logging
        System.out.println("ASSERTION PASSED: Confirmed that order success UI is not displayed.");
    }


    /**
     * Step definition to verify the successful completion of an order.
     * Instead of volatile CSS selectors, we use User-Facing locators (Text and Roles).
     */
    @Then("the order should be successfully placed and confirmed")
    public void verifyOrderIsSuccessfullyPlaced() {
        // 1. Assert the Success Header
        // We look for the bold "ORDER PLACED!" text specifically.
        assertThat(page.getByText("Order Placed!", new Page.GetByTextOptions().setExact(true)))
                .isVisible();

        // 2. Assert the Confirmation Message
        // This confirms the specific success text seen on the UI.
        assertThat(page.getByText("Congratulations! Your order has been confirmed!"))
                .isVisible();

        // 3. Assert functional elements by their Role and Name
        // This ensures that the elements are not just text, but valid clickable links.
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Download Invoice")))
                .isVisible();

        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")))
                .isVisible();
        customer.setActionTimeStamp(LocalDateTime.now());

        // 4. Traceability Logging
        System.out.println("ASSERTION PASSED: Order confirmation verified via UI text and Action Roles.");
        orderAutomationService.sendOrderToOMS();
    }

    @When("the warehouse team processes the order through all lifecycle stages")
    public void theWarehouseTeamProcessesTheOrderThroughAllLifecycleStages() {
        // Authenticate once
        taskAutomation.login("thapelo_oms", "OMS_Secure_2026");

        // This method contains the Stream logic we built previously
        taskAutomation.verifyOrderLifecycle();
    }



    @And("they have proceeded to the checkout page from the cart")
    public void proceedToCheckout() {
        // 1. Perform the action
        cartPage.proceedToCheckout();

        // 2. The "Best Way" includes validation.
        // We verify the URL changes to the checkout/checkout summary page.
        assertTrue(cartPage.getPage().url().contains("/checkout"),
                "Navigation to Checkout page failed! Current URL: " + cartPage.getPage().url());
    }





    @Then("the customer should receive shipping and delivery notifications")
    public void theCustomerShouldReceiveNotifications() {
        // This is where you'd call a mail utility or check an email log table
        // to verify that handleStatusNotifications(order) was triggered
        val item =email.getEmailAfter(customer.getActionTimeStamp());
        int it=0;

    }



}
