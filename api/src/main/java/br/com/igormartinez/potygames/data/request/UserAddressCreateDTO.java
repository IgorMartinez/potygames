package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserAddressCreateDTO (
    @NotNull(message = "The id of user must be provided.")
    @Positive(message = "The id of user must be a positive number.")
    Long idUser,
    
    Boolean favorite,
    Boolean billingAddress,
    String description,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String country,
    String zipCode
) {}
