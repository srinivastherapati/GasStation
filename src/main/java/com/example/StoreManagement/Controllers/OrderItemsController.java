package com.example.StoreManagement.Controllers;

import com.example.StoreManagement.Model.OrderItems;
import com.example.StoreManagement.Model.Products;
import com.example.StoreManagement.Repositories.OrderItemRepo;
import com.example.StoreManagement.Repositories.ProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("order-items")
@CrossOrigin
public class OrderItemsController {
    @Autowired
     private OrderItemRepo orderItemRepo;
    @Autowired
    private ProductsRepo productsRepo;

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestParam String productId
                                          // @RequestParam(required = false) int quantity
    ) {
        Products product = productsRepo.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        OrderItems existingCartItem=orderItemRepo.findByProductIdAndOrderId(productId,null);
        if (existingCartItem != null) {
            // Update quantity of the existing item
            int newQuantity = existingCartItem.getQuantity() + 1;

            // Ensure stock availability for the new quantity
            if (product.getStock() < newQuantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested quantity exceeds available stock");
            }

            existingCartItem.setQuantity(newQuantity);
            orderItemRepo.save(existingCartItem);
            return ResponseEntity.ok(existingCartItem);
        } else {
        // Create and save the OrderItem
        OrderItems orderItem = new OrderItems();
        orderItem.setProductId(productId);
        orderItem.setProductName(product.getName()); // Fetch name from product
        orderItem.setQuantity(1);
        orderItem.setPriceAtOrder(product.getPrice()); // Use product price as priceAtOrder
        orderItemRepo.save(orderItem);

        return ResponseEntity.ok(orderItem);
    }

    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String id) {
        OrderItems item = orderItemRepo.findById(id).orElse(null);
        if (item == null || item.getOrderId() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found in cart");
        }
        orderItemRepo.delete(item);
        return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/cart")
    public ResponseEntity<?> viewCart() {
        List<OrderItems> cartItems = orderItemRepo.findByOrderId(null);
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart is empty");
        }
        return ResponseEntity.ok(cartItems);
    }

}
