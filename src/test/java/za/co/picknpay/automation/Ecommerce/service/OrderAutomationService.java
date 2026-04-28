package za.co.picknpay.automation.Ecommerce.service;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.spring.ScenarioScope;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.picknpay.automation.Ecommerce.API.InvoiceService;
import za.co.picknpay.automation.Ecommerce.API.SearchServiceAPI;
import za.co.picknpay.automation.Ecommerce.Page.AccountDetailsPage;
import za.co.picknpay.automation.Ecommerce.config.Thread.Customer;
import za.co.picknpay.automation.Ecommerce.models.product.Product;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Service
@ScenarioScope
public class OrderAutomationService {

    @Autowired
    private APIRequestContext apiRequestContext;

    @Autowired
    SearchServiceAPI searchServiceAPI;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    Customer customer;

    @Autowired
    AccountDetailsPage accountDetailsPage;


    public String generateUUIDOrder() {
        // Example output: PNP-A1B2C3D4
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "PNP-" + uuid;
    }
    public void sendOrderToOMS() {
        val order=generateUUIDOrder();
        Product product=searchServiceAPI.getALLProducts().stream().findFirst().orElse(null);
        // 1. Prepare the JSON Payload
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderNumber",order );
        orderData.put("customerName", customer.getName());
        orderData.put("email", customer.getEmail());
        orderData.put("address", accountDetailsPage.getAddressString()); // Playwright automatically nests the Address object
        orderData.put("invoicePdf", invoiceService.generateInvoice(order,accountDetailsPage.getAddressString(),
                Arrays.asList(product)));   // Sending empty byte array for now

        // 2. Execute the POST request
        APIResponse response = apiRequestContext.post("http://localhost:8080/api/oms/receive",
                RequestOptions.create().setData(orderData));

        // 3. Validate the connection
        if (response.status() == 200) {
            System.out.println("Success: " + response.text());
        } else {
            System.err.println("Failed to send order. Status: " + response.status() + " | Body: " + response.text());
            throw new RuntimeException("API Ingestion Failed");
        }
    }
}
