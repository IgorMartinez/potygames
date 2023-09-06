package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateDTO (
    @NotNull(message = "The id of product type must be provided.")
    @Positive(message = "The id of product type must be a positive number.")
    Long idProductType,

    @NotBlank(message = "The name must not be blank.")
    String name,

    String description
) {}
