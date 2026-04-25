package za.co.picknpay.automation.Ecommerce.definition;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.picknpay.automation.Ecommerce.Page.AccountDetailsPage;
import za.co.picknpay.automation.Ecommerce.Page.LoginPage;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;

import java.util.Map;

public class RegistrationStepDefinitions {

    @Autowired
    private AccountDetailsPage accountDetailsPage;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    Customer customer;

    @Given("a new customer is successfully registered on the GUI channel with the following details:")
    public void registerNewCustomer(DataTable dataTable) {
        // Convert the 2-column table into a Map
        Map<String, String> data = dataTable.asMap(String.class, String.class);
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
    }
}
