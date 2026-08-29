package com.lets_play.demo.mapper;

import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.dto.request.RegisterRequest;
import com.lets_play.demo.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

    @Override
    public User toEntity(RegisterRequest userRequest) {
        if (userRequest == null) {
            return null;
        }

        return User.builder()
            .name(userRequest.name())
            .email(userRequest.email())
            .password(userRequest.password())
            .build();
    }
}