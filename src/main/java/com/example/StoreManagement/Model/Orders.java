package com.example.StoreManagement.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Orders")
@Data
public class Orders {
    @Id
    private String id;
    private String customerId;
    private Date orderDate;
    private String status;
    private double totalAmount;
    private String deliveryOption;
    private List<String> orderItemIds;
}
