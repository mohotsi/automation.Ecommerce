package za.co.picknpay.automation.Ecommerce.config;



import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Custom retry logic for TestNG.
 * This class decides whether a failed test scenario should be executed again
 * before being officially marked as "Failed" in the reports.
 */
public class SpecificErrorRetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    /**
     * Standard retry limit.
     * Setting this to 1 means the test will run twice in total (Original + 1 Retry).
     */
    private static final int MAX_RETRY_COUNT = 1;

    /**
     * ERROR FILTERING:
     * We don't want to retry everything. For example, if a developer marked a step
     * as 'Pending', retrying it is a waste of time and resources.
     */
    private static final String SPECIFIC_ERROR_MESSAGE = "TimeoutError";

    /**
     * The core logic invoked by TestNG every time a scenario fails.
     * @param result Contains information about the test failure, including the StackTrace.
     * @return true if the test should be rerun, false otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {

        // Ensure we haven't exceeded our maximum allowed retries
        if (retryCount < MAX_RETRY_COUNT) {

            // Check if there is actually an error/exception attached to this result
            if (result.getThrowable() != null && result.getThrowable().getMessage() != null) {

                // CRITICAL FILTER: Only retry if the error is NOT a 'PendingException'.
                // This saves execution time on incomplete test scripts.
                if (!result.getThrowable().getMessage().contains(SPECIFIC_ERROR_MESSAGE)) {

                    System.out.println(">>> FLAKINESS DETECTED <<<");
                    System.out.println("Retrying scenario: " + result.getName());
                    System.out.println("Attempt Number: " + (retryCount + 1));
                    System.out.println("Failure Reason: " + result.getThrowable().getMessage());

                    retryCount++;
                    return true; // TestNG will now trigger the scenario again.
                }
            }
        }

        // If we reach this point, either we hit the retry limit or the error was one we don't want to retry.
        return false;
    }
}
