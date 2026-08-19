package com.lets_play.demo.dto.response;

import java.time.Instant;

public record ProductResponse(
    String id,
    String name,
    String description,
    Double price,
    String userId,
    Instant createdAt,
    Instant updatedAt
) {}