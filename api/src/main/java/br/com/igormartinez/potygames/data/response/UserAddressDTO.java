package br.com.igormartinez.potygames.data.response;

public record UserAddressDTO(
    Long id,
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
