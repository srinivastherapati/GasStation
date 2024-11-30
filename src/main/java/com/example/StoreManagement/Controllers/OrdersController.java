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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private OrderItemRepo orderItemsRepo;
    @Autowired
    private PaymentsRepo paymentsRepo;
    @Autowired
    private ProductsRepo productsRepo;

    // 1. Place an order (from cart items)
    @PostMapping("/place/{customerId}")
    public ResponseEntity<?> placeOrder(@PathVariable String customerId) {
        // Find items in cart
        List<OrderItems> cartItems = orderItemsRepo.findByOrderId(null);
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart is empty");
        }

        // Calculate total amount
        double totalAmount = cartItems.stream().mapToDouble(item -> item.getPriceAtOrder() * item.getQuantity()).sum();

        // Create new order
        Orders order = new Orders();
        order.setCustomerId(customerId);
        order.setOrderDate(new Date());
        order.setStatus("PLACED");
        order.setTotalAmount(totalAmount);
        order.setOrderItemIds(cartItems.stream().map(OrderItems::getId).toList());
        order.setId(cartItems.get(0).getOrderId());
        ordersRepo.save(order);
        cartItems.forEach((cartItem)->{
            Products products=productsRepo.findById(cartItem.getProductId()).orElse(null);
            if(products!=null){
            products.setQuantity(products.getQuantity()-cartItem.getQuantity());
            productsRepo.save(products);
            }
        });
        cartItems.forEach(item -> item.setOrderId(order.getId()));
        orderItemsRepo.saveAll(cartItems);

        return ResponseEntity.ok("Order placed successfully with ID: " +order);
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerOrders(@PathVariable String customerId) {
        List<Orders> orders = ordersRepo.findByCustomerId(customerId);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found for this customer");
        }

        // Fetch order details with associated products
        List<Map<String, Object>> response = orders.stream().map(order -> {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", order.getId());
            orderDetails.put("totalPayment", order.getTotalAmount());

            // Fetch products from OrderItems
            List<OrderItems> items = orderItemsRepo.findByOrderId(order.getId());
            List<Map<String, Object>> products = items.stream().map(item -> {
                Map<String, Object> productDetails = new HashMap<>();
                Products product = productsRepo.findById(item.getProductId()).orElse(null);
                if (product != null) {
                    productDetails.put("productId", product.getId());
                    productDetails.put("name", product.getName());
                }
                productDetails.put("quantityBought", item.getQuantity());
                return productDetails;
            }).collect(Collectors.toList());

            orderDetails.put("products", products);
            return orderDetails;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
        Orders order = ordersRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        // Fetch items in the order
        List<OrderItems> items = orderItemsRepo.findByOrderId(orderId);
        return ResponseEntity.ok(items);
    }
    @PostMapping("/cancel-order/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        // Retrieve the payment for the given order
        Optional<Payments> paymentOptional = paymentsRepo.findByOrderId(orderId);
        if (paymentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found for this order");
        }

        Payments payment = paymentOptional.get();

        // Update the payment status
        payment.setStatus("REFUNDED");
        paymentsRepo.save(payment);

        // Process refund if payment method is card
        if ("CARD".equalsIgnoreCase(payment.getPaymentMethod())) {
            // Here you can implement refund logic with a payment gateway (e.g., Stripe, PayPal)
            // For now, we'll just simulate a successful refund.
            System.out.println("Refund of amount " + payment.getTotalAmount() + " processed successfully");
        }

        Optional<Orders> orderOptional = ordersRepo.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
        Orders order = orderOptional.get();
        order.setStatus("CANCELLED");
        ordersRepo.save(order);

        // Update product quantities for items in the cancelled order
        List<OrderItems> orderItems = orderItemsRepo.findByOrderId(orderId);
        for (OrderItems item : orderItems) {
            Optional<Products> productOptional = productsRepo.findById(item.getProductId());
            if (productOptional.isPresent()) {
                Products product = productOptional.get();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productsRepo.save(product);
            }
        }

        return ResponseEntity.ok("Order cancelled and refund processed successfully");
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        Orders order = ordersRepo.findById(orderId).orElse(null);
        if (order == null || order.getStatus().equals("CANCELLED")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
        order.setStatus(status);
        ordersRepo.save(order);
        return ResponseEntity.ok("Order status updated to " + status);
    }

}
