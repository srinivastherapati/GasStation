package com.example.StoreManagement.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Payments")
@Data
public class Payments {
    @Id
    private String id;
    private String userId;
    private String orderId;
    private String paymentMethod;
    private String status;
    private double totalAmount;
}
