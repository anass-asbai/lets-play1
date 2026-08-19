package com.lets_play.demo.mapper;

import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    Product toEntity(CreateProductRequest request);

    // هاد الدالة كتاخد الريكويست وكتابديتي بيه Entity ديجا موجودة
    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}