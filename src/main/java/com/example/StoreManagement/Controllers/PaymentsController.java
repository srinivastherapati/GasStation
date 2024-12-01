package com.example.StoreManagement.Controllers;

import com.example.StoreManagement.Model.OrderItems;
import com.example.StoreManagement.Model.Orders;
import com.example.StoreManagement.Model.Payments;
import com.example.StoreManagement.Model.Products;
import com.example.StoreManagement.Repositories.OrderItemRepo;
import com.example.StoreManagement.Repositories.OrdersRepo;
import com.example.StoreManagement.Repositories.PaymentsRepo;
import com.example.StoreManagement.Repositories.ProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@CrossOrigin
public class PaymentsController {
    @Autowired
    private PaymentsRepo paymentsRepo;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private OrderItemRepo orderItemsRepo;

    @Autowired
    private ProductsRepo productsRepo;

    // 1. Make a Payment
    @PostMapping("/pay/{customerId}")
    public ResponseEntity<?> makePayment(
            @PathVariable String customerId,
            @RequestBody Payments paymentRequest) {

        // Fetch cart items (orderId = null) for the customer
        List<OrderItems> cartItems = orderItemsRepo.findByOrderId(null);
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart is empty. Add items to the cart before making payment.");
        }

        // Calculate total amount
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtOrder())
                .sum();

        // Validate payment details (basic validation here; enhance as needed)
//        if (paymentRequest.getPaymentMethod() == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment details.");
//        }

        // Create a new orderId for this payment
        String orderId = UUID.randomUUID().toString();

        // Create a payment record
        Payments payment = new Payments();
        payment.setUserId(customerId);
        payment.setOrderId(orderId);
        payment.setPaymentMethod("CARD");
        payment.setStatus("COMPLETED");
        payment.setTotalAmount(totalAmount);

        // Save payment record to Payments collection
        paymentsRepo.save(payment);

        // Update cart items with the orderId
        for (OrderItems item : cartItems) {
            item.setOrderId(orderId);
        }
        orderItemsRepo.saveAll(cartItems);
        // Create new order
        Orders order = new Orders();
        order.setCustomerId(customerId);
        order.setOrderDate(new Date());
        order.setStatus("PLACED");
        order.setTotalAmount(totalAmount);
        order.setOrderItemIds(cartItems.stream().map(OrderItems::getId).toList());
        order.setId(orderId);
        cartItems.forEach((cartItem)->{
            Products products=productsRepo.findById(cartItem.getProductId()).orElse(null);
            if(products!=null){
                products.setStock(products.getStock()-cartItem.getQuantity());
                productsRepo.save(products);
            }
        });
        ordersRepo.save(order);

        return ResponseEntity.status(HttpStatus.CREATED).body("Payment completed successfully. Order Placed: ");
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId) {
        Optional<Payments> payment = paymentsRepo.findById(paymentId);
        if (payment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        }
        return ResponseEntity.ok(payment.get());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUser(@PathVariable String userId) {
        List<Payments> payments = paymentsRepo.findByUserId(userId);
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No payments found for this user");
        }
        return ResponseEntity.ok(payments);
    }
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPayments() {
        List<Payments> payments = paymentsRepo.findAll();
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No payments found");
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/refunds")
    private  ResponseEntity<?> getAllRefunds(){
        List<Payments> refundList= paymentsRepo.findByStatus("REFUNDED");
        if(refundList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Refunds available");
        }
        return ResponseEntity.ok(refundList);
    }

}
