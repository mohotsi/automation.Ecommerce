package za.co.monateRetail.automation.Ecommerce.Page;

import com.microsoft.playwright.Page;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.monateRetail.automation.Ecommerce.config.Thread.Customer;

@ScenarioScope
@Component
public class LoginPage {

    @Autowired
    private Customer customer;

    @Autowired
    private Page page;

    public void newUserSignUp(){
        page.getByText("Signup / Login").first().click();
        page.locator("//input[@placeholder=\"Name\"]").last()
                .fill(customer.getName());
        page.locator("//input[@placeholder=\"Email Address\"]").last()
                .fill(customer.getEmail());
        page.locator("//button[text()='Signup']").last().click();


    }


}
