package com.lets_play.demo.dto.request;

package com.letsplay.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
    @NotBlank(message = "Product name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    Double price
) {}