package br.com.igormartinez.potygames.data.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryItemCreateDTO (
    @NotNull(message = "The id of product must be provided.")
    @Positive(message = "The id of product must be a positive number.")
    Long product,

    @NotBlank(message = "The version must be not blank.")
    String version,

    String condition,

    @Digits(integer = 10, fraction = 2, message = "The price must have up to 10 integer digits and 2 decimal digits of precision.")
    @Min(value = 0, message = "The price must be null, zero or positive.")
    BigDecimal price,

    @NotNull(message = "The quantity must be provided.")
    @Min(value = 0, message = "The quantity must be zero or positive.")
    Integer quantity
) {}
