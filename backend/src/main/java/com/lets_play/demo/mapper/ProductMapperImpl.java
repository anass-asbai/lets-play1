package com.lets_play.demo.mapper;

import com.lets_play.demo.domain.entity.Product;
import com.lets_play.demo.dto.request.CreateProductRequest;
import com.lets_play.demo.dto.request.UpdateProductRequest;
import com.lets_play.demo.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getUserId(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }

    @Override
    public Product toEntity(CreateProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .category(request.category())
            .build();
    }

    @Override
    public void updateEntityFromRequest(UpdateProductRequest request, Product product) {
        if (request == null || product == null) {
            return;
        }

        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.category() != null) {
            product.setCategory(request.category());
        }
    }
}