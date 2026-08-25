package com.lets_play.demo.dto.request;

import com.lets_play.demo.domain.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        String name,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        Role role
) {}