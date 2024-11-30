package com.example.StoreManagement.Repositories;


import com.example.StoreManagement.Model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer,String > {
Customer findByEmail(String email);
}
