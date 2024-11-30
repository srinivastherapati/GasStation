package com.example.StoreManagement.Service;

import com.example.StoreManagement.Model.Products;
import com.example.StoreManagement.Repositories.ProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepo productsRepo;

    public boolean isProductExists(String name){
        Products products=productsRepo.findByName(name);
        return  products!=null;
    }
    public void deleteProductByName(String name) {
        productsRepo.deleteByName(name);
    }
    public Optional<Products> getProductById(String id) {
        return productsRepo.findById(id);
    }

    public List<Products> getProductsSortedByPriceAsc() {
        return productsRepo.findAllByOrderByPriceAsc();
    }

    public List<Products> getProductsSortedByPriceDesc() {
        return productsRepo.findAllByOrderByPriceDesc();
    }
}
