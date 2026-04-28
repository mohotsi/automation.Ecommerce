package za.co.picknpay.automation.Ecommerce.dto;



import java.util.List;

public record OrderRequestDTO(
        String orderNumber,
        String email,
        String customerName,
        String address,
        byte[] invoicePdf
) {}