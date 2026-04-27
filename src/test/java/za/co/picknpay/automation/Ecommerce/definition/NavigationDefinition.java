package za.co.picknpay.automation.Ecommerce.definition;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import za.co.picknpay.automation.Ecommerce.API.SearchServiceAPI;
import za.co.picknpay.automation.Ecommerce.Page.ProductPage;
import za.co.picknpay.automation.Ecommerce.models.product.Product;
import java.util.stream.IntStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.testng.Assert.assertTrue;

public class NavigationDefinition {

    @Autowired
    SearchServiceAPI searchServiceAPI;
    @Autowired
    Page page;

    @Autowired
    ProductPage productPage;

    @Value("${application.url}")
    private String url;
    @And("I search for a product")
    public void iSearchForAProduct() {
        page.navigate(url+"/products");

     Product product= searchServiceAPI.getALLProducts().stream()
             .findFirst()
              .orElseThrow(()-> new NoSuchElementException("There are no products on this webpage"));
        Locator searchInput=page.locator("#search_product");
        searchInput.fill(product.getName());
        searchInput.press("Enter");

    }



    /**
     * Adds a specific product to the cart by synchronizing API data with the UI.
     * This version uses Java Streams to eliminate imperative loops and improve readability.
     */
    @And("I add the product to cart")
    public void iAddTheProductToCart() {
        // 1. DATA SOURCE OF TRUTH: Fetch product details via API POJO.
        Product apiProduct = searchServiceAPI.getALLProducts().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("CRITICAL: No products returned from search API."));

        String targetName = apiProduct.getName();
        System.out.println("Targeting Product from API: " + targetName);

        // 2. UI SYNCHRONIZATION: Locate all product name elements.
        Locator productNamesLocator = page.locator(".productinfo p");
        productNamesLocator.first().waitFor(); // Ensure elements are rendered

        List<String> uiProductNames = productNamesLocator.allTextContents();

        // 3. STREAM LOGIC: Find the index where the UI text matches the API product name.
        // We use IntStream to iterate over indices and filter based on name matching.
        int targetIndex = IntStream.range(0, uiProductNames.size())
                .filter(i -> uiProductNames.get(i).equalsIgnoreCase(targetName))
                .findFirst() // Stops as soon as a match is found (efficient)
                .orElseThrow(() -> new NoSuchElementException("UI-API DESYNC: Product '" + targetName +
                        "' exists in API but is missing from the Web Storefront."));

        // 4. ACTION: Interact with the specific "Add to Cart" button at that index.
        // The .nth() method ensures we target the exact button relative to the matched name.
        page.locator(".productinfo .add-to-cart").nth(targetIndex).click();

        System.out.println("SUCCESS: Added '" + targetName + "' to cart using index: " + targetIndex);
    }
    @And("They navigate to cart")
    public void addProductAndGoToCart() {



        productPage.navigateToCart();

        // Optional: Assert that we are actually on the cart page
        assertTrue(page.url().contains("/view_cart"), "Failed to navigate to the Cart page!");
    }

}
