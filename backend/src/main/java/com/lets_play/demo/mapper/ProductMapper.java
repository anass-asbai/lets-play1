package com.lets_play.demo.mapper;

import com.lets_play.demo.domain.entity.Product;
import com.lets_play.demo.dto.request.CreateProductRequest;
import com.lets_play.demo.dto.request.UpdateProductRequest;
import com.lets_play.demo.dto.response.ProductResponse;
import org.mapstruct.MappingTarget;


public interface ProductMapper {

    ProductResponse toResponse(Product product);

    Product toEntity(CreateProductRequest request);

    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}