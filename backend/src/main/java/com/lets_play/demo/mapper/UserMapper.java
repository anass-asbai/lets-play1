package com.lets_play.demo.mapper;

import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.dto.request.RegisterRequest;
import com.lets_play.demo.dto.response.UserResponse;


public interface UserMapper {
    
    UserResponse toResponse(User user);
    User toEntity(RegisterRequest userRequest);
}