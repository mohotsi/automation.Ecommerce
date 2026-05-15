package za.co.monateRetail.automation.Ecommerce.definition;


import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import za.co.monateRetail.automation.Ecommerce.AutomationEcommerceApplication; // Your main SpringBoot class

@CucumberContextConfiguration
@SpringBootTest(classes = AutomationEcommerceApplication.class)
public class CucumberSpringConfiguration {
    // This class remains empty.
    // It simply tells Cucumber to start the Spring context before running tests.
}
