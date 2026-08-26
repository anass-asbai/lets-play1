package com.lets_play.demo.mapper;
import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.dto.request.RegisterRequest;
import com.lets_play.demo.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserResponse toResponse(User user);
    User toEntity(RegisterRequest userRequest);
}