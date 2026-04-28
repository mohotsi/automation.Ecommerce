package za.co.picknpay.automation.Ecommerce.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "oms_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    private String orderNumber; // e.g., PNP-1714342342

    private String customerEmail;
    private String customerName;

    @Column(length = 1000)
    private String shippingAddress;

    private String status; // PLACED, PICKING, PACKING, SHIPPING, DELIVERED

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] invoicePdf;
}