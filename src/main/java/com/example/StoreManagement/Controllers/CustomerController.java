package com.example.StoreManagement.Controllers;


import com.example.StoreManagement.Model.Customer;
import com.example.StoreManagement.Repositories.CustomerRepository;
import com.example.StoreManagement.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {
    @Autowired
    private CustomerRepository customersRepo;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> customerRegister(@RequestBody Customer customers){
        if(customers.getEmail()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email is required");
        }
        if(customers.getEmail().equals("admin@admin.com") || customers.getEmail().equals("admin@gmail.com")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't signup with email " + customers.getEmail() );
        }
        if(customers.getPhoneNumber()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("phone number is required");
        }
        if(customers.getPassword()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password is required");
        }
        if(customerService.isUserExist(customers.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("customer already registered with email "+customers.getEmail());
        }
        customers.setRole("customer");
        customersRepo.save(customers);
        return ResponseEntity.status(HttpStatus.CREATED).body("customer registered successfully");
    }

    @PostMapping("/login")
    public  ResponseEntity<?> userLogin(@RequestBody Customer customer){
        String email = customer.getEmail();
        String password = customer.getPassword();

        // Check if the provided credentials match the admin credentials
        if (email.equals("admin@admin.com") && password.equals("admin@123")) {
            Map<String, String> adminDetails = new HashMap<>();
            adminDetails.put("emailId", email);
            adminDetails.put("role", "admin");
            return ResponseEntity.ok(adminDetails);
        }
        Customer existingCustomer = customersRepo.findByEmail(email);
        if (existingCustomer != null) {
            // Check if the password matches
            if (!existingCustomer.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email password mismatch");
            }
            // Return customer details
            Map<String, String> customerDetails = new HashMap<>();
            customerDetails.put("emailId", existingCustomer.getEmail());
            customerDetails.put("userName", existingCustomer.getUserName());
            customerDetails.put("role", "customer");
            customerDetails.put("userId",customer.getId());
            return ResponseEntity.ok(customerDetails);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
    }

//    @GetMapping("getAll")
//    public ResponseEntity<?> getAllCustomers(){
//
//    }

}
