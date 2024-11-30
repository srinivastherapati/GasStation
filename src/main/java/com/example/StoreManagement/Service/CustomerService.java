package com.example.StoreManagement.Service;


import com.example.StoreManagement.Model.Customer;
import com.example.StoreManagement.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customersRepo;
    public boolean isUserExist(String email) {
        Customer existingCustomer = customersRepo.findByEmail(email);
        return existingCustomer!=null;
    }
}
