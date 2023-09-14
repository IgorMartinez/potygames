package br.com.igormartinez.potygames.data.response;

public record OrderAddressResponseDTO(
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String country,
    String zipCode
) {}
