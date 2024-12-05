package com.example.StoreManagement.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Products")
@Data
public class Products {
    @Id
    private String id;
    private String name;
    private  String imageUrl;
    private String description;
    private  String  units;
    private double price;
    private int stock;
    private String  category;
    private String promotionCode;
    private double rating;
}
