package com.example.StoreManagement.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "OrderItems")
@Data
public class OrderItems {
    @Id
    private String id;
    private String productId;
    private String orderId;
    private String productName;
    private Integer quantity;
    private double priceAtOrder;
   // private String status;
}
