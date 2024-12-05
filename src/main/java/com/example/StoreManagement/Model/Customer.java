package com.example.StoreManagement.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Customers")
@Data
public class Customer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String DOB;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private List<String> address;
}
