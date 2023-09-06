package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductTypeUpdateDTO (
    @NotNull(message = "The id must be provided.")
    @Positive(message = "The id must be a positive number.")
    Long id,
    
    @NotBlank(message = "The keyword must not be blank.")
    String keyword,

    @NotBlank(message = "The description must not be blank.")
    String description
) {}
