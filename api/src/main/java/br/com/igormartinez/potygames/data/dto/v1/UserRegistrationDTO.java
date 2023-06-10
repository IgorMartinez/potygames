package br.com.igormartinez.potygames.data.dto.v1;

public record UserRegistrationDTO (
    String name,
    String email,
    String password
) { }
