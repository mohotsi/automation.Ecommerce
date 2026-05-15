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
public class ProductPage {

    private final Page page;

    // Locators for adding and navigating

    private final Locator viewCartModalLink;
    private final Locator navCartButton;
     @Autowired
    public ProductPage(Page page) {
        this.page = page;



        // The link that appears in the 'Added!' pop-up modal
        this.viewCartModalLink = page.locator("u:has-text('View Cart')");

        // The main header navigation link
        this.navCartButton = page.locator("header i.fa-shopping-cart");
    }

    /**
     * Scenario 1: Navigate via the Pop-up Modal
     */
    public void navigateToCart() {

        // Waiting for the modal to be visible before clicking
        viewCartModalLink.waitFor();
        viewCartModalLink.click();
    }

    /**
     * Scenario 2: Navigate via the Main Navigation Header
     */
    public void clickCartHeader() {
        navCartButton.click();
    }
}
