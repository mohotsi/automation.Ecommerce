package za.co.picknpay.automation.Ecommerce.config;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.ScenarioScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.Date;

/**
 * Configuration class for Playwright API testing.
 * This class leverages Spring's Dependency Injection to manage the lifecycle of
 * API request contexts and JSON serialization tools.
 */
@Configuration
@EnableAsync // Enables Spring's ability to run tasks asynchronously if required by the framework
public class PlayWrightAPI {

    /**
     * Configures the APIRequestContext for executing REST API calls.
     * * @ScenarioScope: This is critical for test isolation. It ensures that every
     * Cucumber scenario gets its own fresh API context (cookies, headers, state),
     * preventing data leakage between tests.
     * * @Primary: If multiple APIRequestContext beans exist, this is the default
     * choice for @Autowired fields.
     * * @param playwright The underlying Playwright engine (usually a singleton).
     * @return A scoped APIRequestContext ready for HTTP requests.
     */
    @Bean("customerAPIRequestContext")
    @ScenarioScope
    @Primary
    public APIRequestContext apiRequestContext(Playwright playwright) {
        // Initializes a new request context. Note: BaseURL and default headers
        // can be added here if they are global to all API tests.
        return playwright.request().newContext();
    }

    /**
     * Configures a customized Gson bean for JSON (de)serialization.
     * Includes a specialized adapter to handle specific Date formats used by the Shoprite APIs.
     * * @return A configured Gson instance.
     */
    @Bean
    public Gson getGson() {
        return new GsonBuilder()
                // Registers the custom adapter to handle Long (Unix Epoch) to Date conversion
                .registerTypeAdapter(Date.class, UnixEpochDateTypeAdapter.getUnixEpochDateTypeAdapter())
                .create();
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

/**
 * A custom TypeAdapter for GSON that translates Java Date objects to/from Unix Epoch timestamps.
 * This is useful when the API sends/receives time as a long integer (milliseconds)
 * rather than a formatted ISO-8601 string.
 */
class UnixEpochDateTypeAdapter extends TypeAdapter<Date> {

    // Singleton instance of the adapter to save memory and ensure consistency
    private static final TypeAdapter<Date> unixEpochDateTypeAdapter = new UnixEpochDateTypeAdapter();

    private UnixEpochDateTypeAdapter() {
        // Private constructor to enforce the use of the static factory method
    }

    /**
     * Static factory method to retrieve the adapter.
     */
    static TypeAdapter<Date> getUnixEpochDateTypeAdapter() {
        return unixEpochDateTypeAdapter;
    }

    /**
     * Deserialization: Converts a Long value from JSON into a Java Date object.
     * @param in The JSON reader stream.
     * @return A Date object representing the timestamp.
     */
    @Override
    public Date read(final JsonReader in) throws IOException {
        // Reads the next long value (e.g., 1704546000000) and converts to Date
        return new Date(in.nextLong());
    }

    /**
     * Serialization: Converts a Java Date object into a Long (Unix Epoch) for JSON output.
     * @param out The JSON writer stream.
     * @param value The Date object to be converted.
     */
    @Override
    @SuppressWarnings("resource")
    public void write(final JsonWriter out, final Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Extracts milliseconds from the Date and writes it as a numeric value
            out.value(value.getTime());
        }
    }
}
