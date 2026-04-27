package za.co.picknpay.automation.Ecommerce.runner;



import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import za.co.picknpay.automation.Ecommerce.config.SpecificErrorRetryAnalyzer;


/**
 * The Test Runner class for Cash & Carry (Vosloorus region).
 * This acts as the entry point that TestNG uses to discover and execute Cucumber features.
 */
@CucumberOptions(
        // Path to the .feature files specifically for the Vosloorus store/branch.
        features = "src/test/java/za/co/picknpay/automation/Ecommerce/feature",

        // Filter: Only execute scenarios that match these tags.
        tags = "@Regression",
        glue = {"za.co.picknpay.automation.Ecommerce.definition",
                "za.co.picknpay.automation.Ecommerce.config"},

        // Glue links the Gherkin steps to the Java code (Definitions) and the Spring/Playwright setup (Config).


        monochrome = false, // Set to true for readable console output on some CI/CD terminals

        // Reporting plugins: Generates both a JSON (for CI/CD tools) and a user-friendly HTML report.
        plugin = {
                "pretty",
                "json:target/output/Regression.json",
                "html:target/output/Regression.html"
        }
)
public class FullRegression extends AbstractTestNGCucumberTests {

    /**
     * ENABLE PARALLEL EXECUTION
     * By overriding this and setting parallel = true, TestNG will pull the
     * 'data-provider-thread-count' from your XML and run that many scenarios simultaneously.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    /**
     * CUSTOM SCENARIO RUNNER
     * This overrides the default run method to attach the SpecificErrorRetryAnalyzer.
     * * @retryAnalyzer: If a scenario fails, the analyzer checks the error type.
     * If it's a "retriable" error (like a transient network timeout), it will
     * rerun the scenario automatically before marking it as failed.
     */
    @Test(
            groups = "cucumber",
            description = "Runs Cucumber Scenarios",
            dataProvider = "scenarios",
            retryAnalyzer = SpecificErrorRetryAnalyzer.class
    )
    public void runScenario(io.cucumber.testng.PickleWrapper pickleWrapper, io.cucumber.testng.FeatureWrapper featureWrapper) {
        // Delegates the actual execution back to the Cucumber engine.
        super.runScenario(pickleWrapper, featureWrapper);
    }
}