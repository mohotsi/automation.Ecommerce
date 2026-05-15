package za.co.monateRetail.automation.Ecommerce.models.product;

import lombok.Data;

@Data
public class Product {
    private int id;
    private String name;
    private String price;
    private String brand;
    private Category category;
}
