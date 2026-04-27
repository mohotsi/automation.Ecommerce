package za.co.picknpay.automation.Ecommerce.definition;


import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import za.co.picknpay.automation.Ecommerce.Page.AccountDetailsPage;
import za.co.picknpay.automation.Ecommerce.Page.LoginPage;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static za.co.picknpay.automation.Ecommerce.Util.Try;

public class RegistrationStepDefinitions {

    @Autowired
    private AccountDetailsPage accountDetailsPage;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    Customer customer;
    @Autowired
    Page page;
    @Value("${cookies.folder.dir}")
    String COOKIES_DIR;

    @Value("${application.url}")
    private String url;
    @Given("Delete account if it exist")
    public void accountAlreadyIfAccountExist() {
        if(page.getByText("Logged in as").isVisible())
            Try(()->page.getByText("Delete Account").click());
    }

    @Given("a new customer is successfully registered on the GUI channel with the following details:")
    public void registerNewCustomer(DataTable dataTable) throws IOException {
        // Convert the 2-column table into a Map
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        page.navigate(url+"/login");
        loginPage.newUserSignUp();

        // Fill Account Information (Date of Birth only)
        accountDetailsPage.fillAccountInformation(
               customer.getPassword() , // Password skipped as requested
                "15", "May", "1985"
        );

        // Fill Address Information (Names skipped as requested)
        accountDetailsPage.fillAddressDetails(
                customer.getName(), // First Name skipped
                customer.getLastName(), // Last Name skipped
                data.get("Company"),
                data.get("Address 1"),
                "", // Address 2 skipped
                data.get("Country"),
                data.get("State"),
                data.get("City"),
                data.get("Zipcode"),
                data.get("Mobile")
        );

        accountDetailsPage.clickCreate();
        page.getByText("Continue").click();

        Path path = Paths.get(COOKIES_DIR
                +customer.getEmail()+"BrowserCookies.json");
        if((Files.notExists(path)))
            Files.createFile(path);

        page.context().storageState(new BrowserContext.StorageStateOptions().
                setPath(path));
    }


}
