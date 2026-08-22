package com.lets_play.demo.service;

import com.lets_play.demo.domain.entity.Product;
import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.domain.repository.ProductRepository;
import com.lets_play.demo.domain.repository.UserRepository;
import com.lets_play.demo.dto.request.CreateProductRequest;
import com.lets_play.demo.dto.request.UpdateProductRequest;
import com.lets_play.demo.dto.response.ProductResponse;
import com.lets_play.demo.mapper.ProductMapper;
import com.lets_play.demo.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final SecurityUtils securityUtils;

    public ProductService(
            ProductRepository productRepository, 
            UserRepository userRepository, 
            ProductMapper productMapper, 
            SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
        this.securityUtils = securityUtils;
    }

    // CREATE
    public ProductResponse createProduct(CreateProductRequest request) {
        String currentUserEmail = securityUtils.getCurrentUserEmail();
        
        // Return 404 NOT FOUND if user doesn't exist
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Product product = productMapper.toEntity(request);
        product.setUserId(currentUser.getId()); 
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    // READ ALL (Public)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // READ ONE (Public)
    public ProductResponse getProductById(String id) {
        // Return 404 NOT FOUND if product doesn't exist
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
                
        return productMapper.toResponse(product);
    }

    // UPDATE (Restricted to Owner or Admin)
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        
        // Check permissions before updating
        checkOwnershipOrAdmin(existingProduct);
        
        productMapper.updateEntityFromRequest(request, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(updatedProduct);
    }

    // DELETE (Restricted to Owner or Admin)
    public void deleteProduct(String id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        
        // Check permissions before deleting
        checkOwnershipOrAdmin(existingProduct);
        
        productRepository.deleteById(id);
    }

    // --- HELPER METHOD FOR AUTHORIZATION ---
    private void checkOwnershipOrAdmin(Product product) {
        String currentUserEmail = securityUtils.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");
        boolean isOwner = product.getUserId().equals(currentUser.getId());

        // If the user is NOT an admin AND NOT the owner, throw a 403 FORBIDDEN error
        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: You can only modify your own products");
        }
    }
}