package za.co.monateRetail.automation.Ecommerce.config;




import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.cucumber.java.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import za.co.monateRetail.automation.Ecommerce.config.Thread.Customer;
import za.co.monateRetail.automation.Ecommerce.config.Thread.CustomerIdentityPool;


import java.nio.file.Path;

import static za.co.monateRetail.automation.Ecommerce.Util.Try;
import static za.co.monateRetail.automation.Ecommerce.Util.eventually;


/**
 * The Hooks class manages the lifecycle of each test scenario.
 * It acts as the bridge between the Spring Framework and Cucumber,
 * handling setup, teardown, evidence collection, and resource reclamation.
 */
public class Hooks implements ApplicationContextAware {

    // Threshold to prevent runaway scenarios in a CI/CD pipeline
    private static final long SCENARIO_TIMEOUT_MINUTES = 12;

    @Autowired
    Page page; // Thread-isolated Playwright Page instance

    @Autowired(required = false)
    private Customer scenarioCustomer; // The leased identity for this specific thread

    @Autowired
    private CustomerIdentityPool customerIdentityPool;

    private static ApplicationContext applicationContext;


  @Value("${application.url}")
  private String url;


    /**
     * Setup logic before each individual Scenario.
     * Initializes tracing to provide a "black box" recording of the test execution.
     */
    @Before
    public void beforeScenario(Scenario scenario) {

        page.navigate(url);

        //
        long startTime = System.currentTimeMillis();
        System.out.println("--- START: @Before hook for scenario: " + scenario.getName() + " ---");

        // --- PLAYWRIGHT TRACING START ---
        // We capture everything: screenshots for visual, snapshots for DOM state, and sources for code context.
        Try(() -> page.context().tracing().start(new Tracing.StartOptions()
                .setName(scenario.getName().replaceAll("[^a-zA-Z0-9\\-]", ""))
                .setTitle(scenario.getName())
                .setSources(true)
                .setSnapshots(true)
                .setScreenshots(true)));



        System.out.println("--- @Before hook completed in: " + (System.currentTimeMillis() - startTime) + " ms ---");
    }

    /**
     * Executes after each individual step.
     * Left empty to maintain high execution speed, but can be used for "Step-by-Step" screenshots if required.
     */
    @AfterStep
    public void AfterStep(){
        // No-op for performance
    }

    /**
     * Teardown logic after each individual Scenario.
     * This is the most critical method for parallel stability.
     */
    @After
    public void afterEachExample(Scenario scenario) {
        long stopStart = System.currentTimeMillis();

        // --- EVIDENCE COLLECTION ---
        // We categorize traces into 'passed' and 'failed' folders to make debugging targeted and fast.
        String subFolder = scenario.isFailed() ? "failed" : "passed";

        // If the scenario failed, embed a physical screenshot into the Cucumber HTML report immediately
        if (scenario.isFailed()) {
            Try(() -> scenario.attach(page.screenshot(), "image/png", "Failure Screenshot"));
        }

        // Generate the trace ZIP file which can be opened in https://trace.playwright.dev/
        val path = Path.of("./target/output/trace/" + subFolder + "/" +
                scenario.getName().trim().replaceAll("[^a-zA-Z0-9\\-]", "") + ".zip");

        Try(() -> page.context().tracing().stop(new Tracing.StopOptions().setPath(path)));

        System.out.println("[TRACE] Saved to: " + path);

        // --- IDENTITY RECLAMATION (The Check-In) ---
        // VERY IMPORTANT: If we don't release the customer here, the pool will "leak"
        // and eventually all accounts will be locked out/unavailable.
        long cleanupStart = System.currentTimeMillis();
        if (scenarioCustomer != null && scenarioCustomer.getOriginalEmail() != null) {
            try {
                // Log the release so we can track user rotation in parallel logs
                System.out.println("RELEASING IDENTITY: " + scenarioCustomer.getOriginalEmail());
                customerIdentityPool.releaseCustomer(scenarioCustomer);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                System.err.println("Thread interrupted during customer release.");
            } catch (Exception e) {
                System.err.println("Unexpected failure while returning user to pool: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // --- BROWSER SHUTDOWN ---
        // Ensures no "zombie" browser processes are left running on the host machine.
        // We close the page first, then the specific context.
        try {
            page.close();
            page.context().browser().close();
        } catch (Exception e) {
            System.out.println("Browser already closed or unreachable.");
        }

        System.out.println("--- @After hook finished in: " + (System.currentTimeMillis() - stopStart) + " ms ---");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Hooks.applicationContext = applicationContext;
    }

    @AfterAll
    public static void afterAll() {
        // Space for final reporting logic (e.g., sending emails, updating Jira/Xray)
    }
}