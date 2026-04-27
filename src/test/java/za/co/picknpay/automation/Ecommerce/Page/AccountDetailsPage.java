package za.co.picknpay.automation.Ecommerce.Page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;

import static za.co.picknpay.automation.Ecommerce.Util.eventually;

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
     @Autowired
    public AccountDetailsPage(Page page) {
        this.page = page;

        // Section 1 Locators
        this.genderMale = page.locator("#id_gender1");
        this.genderFemale = page.locator("#id_gender2");
        this.password = page.locator("#password");
        this.days = page.locator("#days");
        this.months = page.locator("#months");
        this.years = page.locator("#years");

        // Section 2 Locators (Address)
        this.firstName = page.locator("#first_name");
         this.lastName = page.locator("#last_name");
        this.company = page.locator("#company");
        this.address1 = page.locator("#address1");
        this.address2 = page.locator("#address2");
        this.country = page.locator("#country");
        this.state = page.locator("#state");
        this.city = page.locator("#city");
        this.zipcode = page.locator("#zipcode");
        this.mobileNumber = page.locator("#mobile_number");

        this.createAccountButton = page.locator("//button[text()='Create Account']");

    }

    // --- Methods divided by Section ---

    public void fillAccountInformation(String pass, String d, String m, String y) {
         genderMale.waitFor();
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
         eventually(createAccountButton::isEnabled);
        createAccountButton.click();
    }
}