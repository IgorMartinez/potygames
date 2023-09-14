package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotBlank;

public record OrderAddressRequestDTO(
    @NotBlank(message = "The street must be not blank.")
    String street,

    @NotBlank(message = "The number must be not blank.")
    String number,

    String complement,
    
    @NotBlank(message = "The neighborhood must be not blank.")
    String neighborhood,

    @NotBlank(message = "The city must be not blank.")
    String city,

    @NotBlank(message = "The state must be not blank.")
    String state,

    @NotBlank(message = "The country must be not blank.")
    String country,

    @NotBlank(message = "The zip code must be not blank.")
    String zipCode
) {}
