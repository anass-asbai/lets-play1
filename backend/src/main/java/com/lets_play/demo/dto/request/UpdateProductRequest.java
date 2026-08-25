package com.lets_play.demo.dto.request;

import jakarta.validation.constraints.Positive;

public record UpdateProductRequest(
	String name,

	String description,

	@Positive(message = "Price must be greater than 0")
	Double price,

	String category
) {}
