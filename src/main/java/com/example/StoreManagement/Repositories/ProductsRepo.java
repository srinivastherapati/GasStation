package com.example.StoreManagement.Repositories;


import com.example.StoreManagement.Model.Products;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductsRepo extends MongoRepository<Products,String> {
    Products findByName(String name);
    void deleteByName(String name);
    List<Products> findByCategory(String category);
    List<Products> findAllByOrderByPriceAsc();
    List<Products> findAllByOrderByPriceDesc();
}
