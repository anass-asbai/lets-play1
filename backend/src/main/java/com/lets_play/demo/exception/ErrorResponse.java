package com.lets_play.demo.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    int status,
    String message,
    Map<String, String> errors,
    Instant timestamp
) {
    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this(status, message, errors, Instant.now());
    }
}