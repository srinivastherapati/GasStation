package com.example.StoreManagement.Controllers;

import com.example.StoreManagement.Model.Products;
import com.example.StoreManagement.Model.Reviews;
import com.example.StoreManagement.Repositories.ProductsRepo;
import com.example.StoreManagement.Repositories.ReviewsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/review")
@CrossOrigin
public class ReviewController {
    @Autowired
    private ReviewsRepo reviewRepo;
    @Autowired
    private ProductsRepo productsRepo;

    @PostMapping("/add/{userId}/{productId}")
    public ResponseEntity<?> addReview(@PathVariable String userId,
                                       @PathVariable String productId,
                                       @RequestParam int rating,
                                       @RequestParam(required = false) String comment) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be between 1 and 5");
        }

        Reviews review = new Reviews();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment);
        Products products=productsRepo.findById(productId).orElse(null);
        if(products!=null){
        products.setRating((rating));
        productsRepo.save(products);
        }
        reviewRepo.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added successfully");
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id,
                                          @RequestParam int rating,
                                          @RequestParam(required = false) String comment) {
        Optional<Reviews> existingReview = reviewRepo.findById(id);
        if (existingReview.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }

        Reviews review = existingReview.get();
        if (rating < 1 || rating > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be between 1 and 5");
        }
        review.setRating(rating);
        review.setComment(comment);
        reviewRepo.save(review);

        return ResponseEntity.ok("Review updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        Optional<Reviews> review = reviewRepo.findById(id);
        if (review.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
        reviewRepo.deleteById(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable String productId) {
        List<Reviews> reviews = reviewRepo.findByProductId(productId);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this product");
        }
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable String userId) {
        List<Reviews> reviews = reviewRepo.findByUserId(userId);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this user");
        }
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable String productId) {
        List<Reviews> reviews = reviewRepo.findByProductId(productId);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this product");
        }

        double averageRating = reviews.stream()
                .mapToDouble(Reviews::getRating)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok("Average rating: " + averageRating);
    }
}
