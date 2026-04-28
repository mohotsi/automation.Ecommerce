package za.co.picknpay.automation.Ecommerce.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.picknpay.automation.Ecommerce.Service.EmailService;
import za.co.picknpay.automation.Ecommerce.dto.OrderRequestDTO;
import za.co.picknpay.automation.Ecommerce.models.OrderEntity;
import za.co.picknpay.automation.Ecommerce.repository.OrderRepository;

import java.util.List;

@RestController
@RequestMapping("/api/oms")
public class OMSController {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private EmailService emailService;

    /**
     * STAGE 1: RECEIVE
     * Playwright sends data here. No Bearer token needed (Whitelisted in Security).
     */
    @PostMapping("/receive")
    public ResponseEntity<String> receiveOrderFromAutomation(@RequestBody OrderRequestDTO dto) {
        OrderEntity order = new OrderEntity(
                dto.orderNumber(),
                dto.email(),
                dto.customerName(),
                dto.address(),
                "PLACED", // Initial status
                dto.invoicePdf()
        );

        repository.save(order);

        // Notify customer immediately
        emailService.sendNotification(order.getCustomerEmail(),
                "Order Received",
                "Hi " + order.getCustomerName() + ", your order " + order.getOrderNumber() + " has been received!");

        return ResponseEntity.ok("Order " + dto.orderNumber() + " successfully saved to H2 Database.");
    }

    /**
     * STAGE 2: WAREHOUSE TASKS (Picking, Packing, Staging, Shipping)
     * Requires Bearer Token (Authorized for Warehouse Users).
     */
    @PutMapping("/tasks/{orderNumber}/process")
    public ResponseEntity<String> processOrderTask(
            @PathVariable String orderNumber,
            @RequestParam String status) {

        OrderEntity order = repository.findById(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found in database"));

        // Update status logic
        order.setStatus(status.toUpperCase());
        repository.save(order);

        // Trigger notifications based on the "Familiar Patch" of OMS
        handleStatusNotifications(order);

        return ResponseEntity.ok("Order " + orderNumber + " moved to status: " + status.toUpperCase());
    }

    private void handleStatusNotifications(OrderEntity order) {
        String status = order.getStatus();
        switch (status) {
            case "SHIPPING" -> emailService.sendNotification(order.getCustomerEmail(),
                    "Order Dispatched", "Good news! Your order is on its way to " + order.getShippingAddress());
            case "DELIVERED" -> emailService.sendNotification(order.getCustomerEmail(),
                    "Order Delivered", "Your order has been successfully delivered. Enjoy!");
        }
    }

    @GetMapping("/orders")
    public List<OrderEntity> getAllOrders() {
        return repository.findAll();
    }
}