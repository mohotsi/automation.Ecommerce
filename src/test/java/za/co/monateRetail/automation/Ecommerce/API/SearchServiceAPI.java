package za.co.monateRetail.automation.Ecommerce.API;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.monateRetail.automation.Ecommerce.models.product.Product;
import za.co.monateRetail.automation.Ecommerce.models.product.SearchResponse;

import java.util.ArrayList;
import java.util.List;



@ScenarioScope
@Component
public class SearchServiceAPI {


       private static List<Product> products= new ArrayList<>();



        @Autowired
        private APIRequestContext apiRequestContext;

        @Autowired
        private ObjectMapper objectMapper; // Spring Boot provides this automatically

        public List<Product> getProducts(String searchTerm) {
            // 1. Set up the request options with Form Data
            RequestOptions options = RequestOptions.create()
                    .setIgnoreHTTPSErrors(true)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setForm(FormData.create().set("search_product", searchTerm));

            // 2. Execute the POST request
            APIResponse response = apiRequestContext.post("https://automationexercise.com/api/searchProduct", options);

            // 3. Handle the response
            if (response.status() == 200) {
                try {
                    // Map the JSON string directly to our SearchResponse POJO
                    SearchResponse searchResponse = objectMapper.readValue(response.text(), SearchResponse.class);
                    return searchResponse.getProducts();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse API response, re run later after 5 minutes again the website is under pressure", e);
                }
            } else {
                throw new RuntimeException("API request failed with status: " + response.status());
            }
        }
        public List<Product> getALLProducts(){
            if(products.size()>0){
                return products;
            }
            else
                products.addAll(getProducts("top"));
            return products;
        }
    }





