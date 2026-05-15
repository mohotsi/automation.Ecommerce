package za.co.monateRetail.automation.Ecommerce.Page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;

@Getter
@Component
@ScenarioScope
public class CartPage {

    private final Page page;
    private final Locator proceedToCheckoutButton;
     @Autowired
    public CartPage(Page page) {
        this.page = page;
        // Using a CSS selector that targets the button text specifically
        this.proceedToCheckoutButton = page.locator("text=Proceed To Checkout");
    }

    public void proceedToCheckout() {
        proceedToCheckoutButton.click();
    }
}
