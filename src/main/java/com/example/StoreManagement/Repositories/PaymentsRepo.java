package com.example.StoreManagement.Repositories;


import com.example.StoreManagement.Model.Payments;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentsRepo extends MongoRepository<Payments,String > {

    Optional<Payments> findByOrderId(String orderId);
    List<Payments> findByUserId(String userId);
    List<Payments> findByStatus(String status);
}
