package com.lets_play.demo.controller;

import com.lets_play.demo.dto.request.CreateProductRequest;
import com.lets_play.demo.dto.request.UpdateProductRequest;
import com.lets_play.demo.dto.response.ApiResponse;
import com.lets_play.demo.dto.response.ProductResponse;
import com.lets_play.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse<>("Products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse<>("Product retrieved successfully", product));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse newProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Product created successfully", newProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable String id, 
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(new ApiResponse<>("Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        // Changed to .ok() instead of .noContent() so the user can actually see the message body
        return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", null)); 
    }
}