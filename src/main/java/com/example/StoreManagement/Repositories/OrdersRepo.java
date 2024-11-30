package com.example.StoreManagement.Repositories;


import com.example.StoreManagement.Model.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrdersRepo extends MongoRepository<Orders,String> {
    List<Orders> findByCustomerId(String customerId);
}
