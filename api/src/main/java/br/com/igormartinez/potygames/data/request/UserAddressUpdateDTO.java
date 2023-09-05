package br.com.igormartinez.potygames.data.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserAddressUpdateDTO (
    @NotNull(message = "The id of user address must be provided.")
    @Positive(message = "The id of user address must be a positive number.")
    Long id,

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
