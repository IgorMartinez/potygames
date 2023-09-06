package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotBlank;

public record ProductTypeCreateDTO (
    @NotBlank(message = "The keyword must not be blank.")
    String keyword,

    @NotBlank(message = "The description must not be blank.")
    String description
) {}
