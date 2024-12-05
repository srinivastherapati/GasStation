package com.example.StoreManagement.Controllers;


import com.example.StoreManagement.Model.Customer;
import com.example.StoreManagement.Model.Orders;
import com.example.StoreManagement.Repositories.CustomerRepository;
import com.example.StoreManagement.Repositories.OrdersRepo;
import com.example.StoreManagement.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {
    @Autowired
    private CustomerRepository customersRepo;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrdersRepo ordersRepo;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> customerRegister(@RequestBody Customer customers){
        System.out.println("entered register api");
        System.out.println(customers);
        if(customers.getFirstName()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userName is required");
        }
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

        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(customers.getPassword());
        customers.setPassword(encryptedPassword);
        customers.setRole("customer");
        customers.setRole("customer");
        customersRepo.save(customers);
        return ResponseEntity.status(HttpStatus.CREATED).body("customer registered successfully");
    }

    @PostMapping("/login")
    public  ResponseEntity<?> userLogin(@RequestBody Customer customer){
        System.out.println("entered login api");
        String email = customer.getEmail();
        String password = customer.getPassword();


        // Check if the provided credentials match the admin credentials
        if (email.equals("admin@admin.com") && password.equals("admin@123")) {
            Map<String, String> adminDetails = new HashMap<>();
            adminDetails.put("emailId", email);
            adminDetails.put("role", "admin");
            adminDetails.put("userName","admin");
            adminDetails.put("userId","12345678");
            return ResponseEntity.ok(adminDetails);
        }
        Customer existingCustomer = customersRepo.findByEmail(email);
        if (existingCustomer != null) {
            // Check if the password matches
            if (!passwordEncoder.matches(password, existingCustomer.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email password mismatch");
            }
            // Return customer details
            System.out.println(existingCustomer.getFirstName());
            System.out.println(existingCustomer.getId());
            Map<String, String> customerDetails = new HashMap<>();
            customerDetails.put("emailId", existingCustomer.getEmail());
            customerDetails.put("userName", existingCustomer.getFirstName());
            customerDetails.put("role", "customer");
            customerDetails.put("userId",existingCustomer.getId());
            return ResponseEntity.ok(customerDetails);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
    }
    @GetMapping("/get")
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customersRepo.findAll()
                .stream()
                .filter(customer -> !"admin@admin.com".equalsIgnoreCase(customer.getEmail()))
                .toList();
        if (customers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.EMPTY_LIST);
        }

        List<Map<String, Object>> response = customers.stream().map(customer -> {
            Map<String, Object> customerDetails = new HashMap<>();

            // Basic customer details
            customerDetails.put("customerName", customer.getFirstName());
            customerDetails.put("customerEmail", customer.getEmail());

            // Fetch customer-related data from Products collection
            List<Orders> customerOrders = ordersRepo.findByCustomerId(customer.getId());
            customerDetails.put("numberOfOrders",customerOrders.size());

            // Calculate total order value
            double totalOrderValue = customerOrders.stream()
                    .mapToDouble(Orders::getTotalAmount) // Assuming `getPrice()` exists in Product
                    .sum();

            // Get the latest order date
            Optional<Date> lastOrderDate = customerOrders.stream()
                    .map(Orders::getOrderDate) // Assuming `getOrderDate()` exists in Product
                    .max(Date::compareTo);

            customerDetails.put("customerTotalOrderValue", totalOrderValue);
            customerDetails.put("lastOrderDate", lastOrderDate.orElse(null)); // Default to null if no orders exist

            return customerDetails;
        }).toList();

        return ResponseEntity.ok(response);
    }


}
