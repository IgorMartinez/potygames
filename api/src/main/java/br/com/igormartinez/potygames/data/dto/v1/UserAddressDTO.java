package br.com.igormartinez.potygames.data.dto.v1;

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
