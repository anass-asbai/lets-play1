package com.lets_play.demo.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import com.lets_play.demo.domain.entity.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{ 'database_field_name' : ?0 }")
    List<Product> findByCategory(String category);
    Page<Product> findByCategory(String category, Pageable pageable);
}
