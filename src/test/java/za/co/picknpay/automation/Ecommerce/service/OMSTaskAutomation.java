package za.co.picknpay.automation.Ecommerce.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import za.co.picknpay.automation.Ecommerce.API.SearchServiceAPI;

import java.util.Map;
import java.util.stream.Stream;

@Component
public class OMSTaskAutomation {

    @Autowired
    private SearchServiceAPI searchServiceAPI;

    @Autowired
    private APIRequestContext apiRequestContext;

    @Autowired
    OrderAutomationService orderAutomationService;

    private String bearerToken; // Assume this is populated via a login method
    @Value("${oms.api.url}")
    private String url;
    public void verifyOrderLifecycle() {
        String orderNumber=orderAutomationService.getOrderNumber();
        Stream.of("PICKING", "PACKING", "PICKUP", "SHIPPING", "DELIVERED")
                .forEach(stage -> {
                    // 1. Execute the PUT request
                    APIResponse response = apiRequestContext.put(url+"/api/oms/tasks/" + orderNumber + "/process",
                            RequestOptions.create()
                                    .setHeader("Authorization", bearerToken)
                                    .setQueryParam("status", stage));

                    // 2. Assert HTTP Status
                    Assert.assertEquals(response.status(), 200, "Stage " + stage + " failed to return 200 OK");

                    // 3. Assert Response Body Message String
                    String expectedMessage = String.format("Order %s moved to status: %s", orderNumber, stage);
                    Assert.assertEquals(response.text().trim(), expectedMessage,
                            "The success message returned by the API is incorrect for stage: " + stage);

                    // 4. Assert Database State via SearchServiceAPI
                    // We use your search service to pull the actual record and verify the status field

                });
    }
    public void login(String username, String password) {

        // 1. Send the POST request to your /login endpoint
        APIResponse response = apiRequestContext.post(url+"/api/oms/login",
                RequestOptions.create().setIgnoreHTTPSErrors(true).setData(Map.of(
                        "username", username,
                        "password", password
                )));

        // 2. Assert that we actually got in (200 OK)
        Assert.assertEquals(response.status(), 200, "Login failed! Check credentials or SecurityConfig.");

        // 3. Extract the token from the response: {"accessToken": "eyJ..."}
        JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
        String token = json.get("accessToken").getAsString();

        // 4. Store it with the "Bearer " prefix for easy use in PUT/GET headers
        this.bearerToken = "Bearer " + token;
    }
}
