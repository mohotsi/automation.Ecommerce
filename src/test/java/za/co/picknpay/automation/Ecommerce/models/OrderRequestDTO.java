package za.co.picknpay.automation.Ecommerce.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private String orderNumber;
   private String email;
  private   String customerName;
 private    String address;
 private    byte[] invoicePdf;
}
