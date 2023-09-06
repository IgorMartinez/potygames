package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductUpdateDTO (
    @NotNull(message = "The id must be provided.")
    @Positive(message = "The id must be a positive number.")
    Long id,

    @NotNull(message = "The id of product type must be provided.")
    @Positive(message = "The id of product type must be a positive number.")
    Long idProductType,

    @NotBlank(message = "The name must not be blank.")
    String name,

    String description
) {}
