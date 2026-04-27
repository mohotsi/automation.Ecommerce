package za.co.picknpay.automation.Ecommerce.Page;



import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;

@Getter
@Component
@ScenarioScope
public class CheckoutPage {

    private final Page page;

    // Summary Page
    private final Locator placeOrderButton;

    // Payment Page
    private final Locator nameOnCard;
    private final Locator cardNumber;
    private final Locator cvc;
    private final Locator expiryMonth;
    private final Locator expiryYear;
    private final Locator payAndConfirmButton;
     @Autowired
    public CheckoutPage(Page page) {
        this.page = page;

        // Summary step
        this.placeOrderButton = page.locator("text=Place Order");

        // Payment step (Using data-qa for stability)
        this.nameOnCard = page.locator("data-qa=name-on-card");
        this.cardNumber = page.locator("data-qa=card-number");
        this.cvc = page.locator("data-qa=cvc");
        this.expiryMonth = page.locator("data-qa=expiry-month");
        this.expiryYear = page.locator("data-qa=expiry-year");
        this.payAndConfirmButton = page.locator("data-qa=pay-button");
    }

    public void clickPlaceOrder() {
        placeOrderButton.click();
    }

    public void fillPaymentForm(String name, String num, String code, String mm, String yyyy) {
        nameOnCard.fill(name);
        cardNumber.fill(num);
        cvc.fill(code);
        expiryMonth.fill(mm);
        expiryYear.fill(yyyy);
    }

    public void clickPayAndConfirm() {
        payAndConfirmButton.click();
    }
}
