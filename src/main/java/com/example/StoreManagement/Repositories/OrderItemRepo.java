package com.example.StoreManagement.Repositories;


import com.example.StoreManagement.Model.OrderItems;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderItemRepo extends MongoRepository<OrderItems,String > {
    OrderItems findByProductIdAndOrderId(String productId,String orderId);
    List<OrderItems> findByOrderId(String orderId);
}
