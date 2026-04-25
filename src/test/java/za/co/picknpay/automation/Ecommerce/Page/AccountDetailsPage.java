package za.co.picknpay.automation.Ecommerce.Page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import lombok.Getter;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;

@Getter
@Component
@ScenarioScope
public class AccountDetailsPage {

    private final Page page;

    // --- Section 1: Account Information ---
    private final Locator genderMale;
    private final Locator genderFemale;
    private final Locator password;
    private final Locator days;
    private final Locator months;
    private final Locator years;

    // --- Section 2: Address Information ---
    private final Locator firstName;
    private final Locator lastName;
    private final Locator company;
    private final Locator address1;
    private final Locator address2;
    private final Locator country;
    private final Locator state;
    private final Locator city;
    private final Locator zipcode;
    private final Locator mobileNumber;

    // --- Buttons ---
    private final Locator createAccountButton;

    public AccountDetailsPage(Page page) {
        this.page = page;

        // Section 1 Locators
        this.genderMale = page.locator("#id_gender1");
        this.genderFemale = page.locator("#id_gender2");
        this.password = page.locator("data-qa=password");
        this.days = page.locator("data-qa=days");
        this.months = page.locator("data-qa=months");
        this.years = page.locator("data-qa=years");

        // Section 2 Locators (Address)
        this.firstName = page.locator("data-qa=first_name");
        this.lastName = page.locator("data-qa=last_name");
        this.company = page.locator("data-qa=company");
        this.address1 = page.locator("data-qa=address");
        this.address2 = page.locator("data-qa=address2");
        this.country = page.locator("data-qa=country");
        this.state = page.locator("data-qa=state");
        this.city = page.locator("data-qa=city");
        this.zipcode = page.locator("data-qa=zipcode");
        this.mobileNumber = page.locator("data-qa=mobile_number");

        this.createAccountButton = page.locator("data-qa=create-account");
    }

    // --- Methods divided by Section ---

    public void fillAccountInformation(String pass, String d, String m, String y) {
        genderMale.click(); // Defaulting to Male for the test
        password.fill(pass);
        days.selectOption(d);
        months.selectOption(m);
        years.selectOption(y);
    }

    public void fillAddressDetails(String fName, String lName, String comp, String addr1, String addr2,
                                   String countryName, String stateName, String cityName, String zip, String mobile) {
        firstName.fill(fName);
        lastName.fill(lName);
        company.fill(comp);
        address1.fill(addr1);
        address2.fill(addr2);
        country.selectOption(countryName);
        state.fill(stateName);
        city.fill(cityName);
        zipcode.fill(zip);
        mobileNumber.fill(mobile);
    }

    public void clickCreate() {
        createAccountButton.click();
    }
}