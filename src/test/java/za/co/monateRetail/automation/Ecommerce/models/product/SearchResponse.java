package za.co.monateRetail.automation.Ecommerce.models.product;

import lombok.Data;
import java.util.List;

@Data // This generates Getters, Setters, toString, equals, and hashCode
public class SearchResponse {
    private int responseCode;
    private List<Product> products;
}
