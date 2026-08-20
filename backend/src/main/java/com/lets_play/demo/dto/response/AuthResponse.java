package com.lets_play.demo.dto.response;

public record AuthResponse(
    String token,
    UserResponse user
) {}