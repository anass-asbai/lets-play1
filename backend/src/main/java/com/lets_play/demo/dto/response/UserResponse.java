package com.lets_play.demo.dto.response;

import com.lets_play.demo.domain.entity.Role;
import java.time.Instant;

public record UserResponse(
    String id,
    String name,
    String email,
    Role role,
    Instant createdAt
) {}