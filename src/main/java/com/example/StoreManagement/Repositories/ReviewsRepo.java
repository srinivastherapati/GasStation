package com.example.StoreManagement.Repositories;


import com.example.StoreManagement.Model.Reviews;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewsRepo extends MongoRepository<Reviews,String > {
    List<Reviews> findByProductId(String productId);
    List<Reviews> findByUserId(String userId);
    Optional<Reviews> findByUserIdAndProductId(String userId, String productId);
}
