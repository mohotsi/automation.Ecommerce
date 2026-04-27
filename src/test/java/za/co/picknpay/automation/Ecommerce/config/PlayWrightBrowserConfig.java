package za.co.picknpay.automation.Ecommerce.config;



import com.microsoft.playwright.*;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.testng.xml.XmlTest.DEFAULT_TIMEOUT_MS;

/**
 * Configuration class responsible for instantiating the Playwright Page object based on the environment.
 * It supports multi-browser testing (Chrome, Edge, Safari-emulation, Mobile) using Spring profiles/properties.
 */
@Configuration
public class PlayWrightBrowserConfig {

    @Autowired
    Customer customer; // Injected to retrieve user-specific data (like email) for cookie path generation

    @Value("${cookies.folder.dir}")
    String COOKIES_DIR;

    /**
     * Microsoft Edge Configuration.
     * Activates only when 'browser=edge' is set in properties.
     * Uses ScenarioScope to ensure each test starts with a fresh browser instance.
     */
    @ConditionalOnProperty(name = "browser", havingValue = "edge")
    @Bean
    @ScenarioScope
    @Primary
    @Lazy
    public Page edge() throws IOException {
        Playwright playwright = Playwright.create();
        // Launching the actual installed MS Edge channel
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setChannel("msedge")
                .setHeadless(false));

        // BrowserContext allows for session isolation and loading saved cookies/storage
        Page page = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(getPath())).newPage();

        // Granting permissions automatically to avoid browser pop-ups during automation
        List<String> permit = List.of("geolocation", "clipboard-read", "payment-handler", "clipboard-write");
        page.context().grantPermissions(permit);
        page.setViewportSize(1920, 1300);
        return page;
    }

    /**
     * Android/Safari Emulation Configuration.
     * Uses Chromium to mimic an Android device running Chrome/Safari.
     */
    @ConditionalOnProperty(name = "browser", havingValue = "safari")

    @ScenarioScope
    @Primary
    @Lazy
    public Page safari() throws IOException {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // Launch browser with a specific Android User Agent to trigger mobile-responsive web views
        BrowserContext androidContext = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Linux; Android 13; SM-A03s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Mobile Safari/537.36")
                .setStorageStatePath(getPath())
                .setViewportSize(720, 1600) // Typical mobile aspect ratio
        );
        Page page = androidContext.newPage();

        page.context().grantPermissions(List.of("geolocation"));
        return page;
    }

    /**
     * iPhone 14 Pro Emulation.
     * Configures the viewport and User Agent to match iOS specifications.
     */
    @ConditionalOnProperty(name = "browser", havingValue = "iphone14Pro")
    @Bean
    @Primary
    @Lazy
    @ScenarioScope
    public Page iphone14Pro() throws IOException {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setDevtools(false).setHeadless(false));

        String iPhone14ProUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1";

        BrowserContext iPhoneContext = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent(iPhone14ProUserAgent)
                .setIsMobile(true) // Notifies the site that the device is a mobile touch device
                .setStorageStatePath(getPath())
                .setViewportSize(430, 932) // Specific resolution for iPhone 14 Pro
        );
        Page page = iPhoneContext.newPage();
        page.context().grantPermissions(List.of("geolocation"));
        return page;
    }

    /**
     * Google Chrome Configuration (Primary Desktop Browser).
     * Includes extensive timeout settings and security bypasses for stable automation.
     */
    @ConditionalOnMissingBean
    @Bean
    @ScenarioScope
    @Primary
    @Lazy
    public Page chrome() throws IOException {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setChannel("chrome")
                .setTimeout(30000)
                .setHeadless(false)
                .setDevtools(false));

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true) // Useful for testing on environments with self-signed certs
                .setStorageStatePath(getPath()) // Injects existing login session/cookies
                .setBypassCSP(true); // Bypasses Content Security Policy to allow easier script execution/injection

        BrowserContext context = browser.newContext(contextOptions);
        Page page = context.newPage();

        // Standardizing timeouts to prevent flaky tests on slow networks
        page.setDefaultTimeout(60000*2); // Max time for any action (click, fill, etc.)
        page.setDefaultNavigationTimeout(60000*2); // Max time for page loads

        page.context().grantPermissions(List.of("geolocation"));
        page.setViewportSize(1920, 1300);
        return page;
    }

    /**
     * Generates a unique path for the browser's storage state (cookies/session storage).
     * The path is unique to the Customer's email, allowing for parallel testing with different users
     * without their sessions overwriting each other.
     * * @return Path to the JSON file where cookies are stored.
     */
    private Path getPath() throws IOException {
        // Constructing path based on customer email: e.g., user@shoprite.co.zaBrowserCookies.json
        Path path = Paths.get(COOKIES_DIR
                + customer.getEmail() + "BrowserCookies.json");

        // Ensures the file exists so Playwright doesn't throw an error when attempting to read/write it
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        return path;
    }
}