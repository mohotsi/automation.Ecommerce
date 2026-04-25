package za.co.picknpay.automation.Ecommerce.Page;

import com.microsoft.playwright.Page;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;

@ScenarioScope
public class LoginPage {

    @Autowired
    private Customer customer;

    @Autowired
    private Page page;

    public void newUserSignUp(){
        page.locator("//input[@placeholder=\"Email Address\"]").last()
                .fill(customer.getName());
        page.locator("//input[@placeholder=\"Email Address\"]").last()
                .fill(customer.getEmail());


    }


}
