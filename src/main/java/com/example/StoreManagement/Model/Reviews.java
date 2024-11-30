package com.example.StoreManagement.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Reviews")
@Data
public class Reviews {
    @Id
    private String id;
    private String userId;
    private String productId;
    private double rating;
    private String comment;
}
