package br.com.igormartinez.potygames.data.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationDTO (
    @NotBlank(message = "The email must be not blank.")
    String email,

    @NotBlank(message = "The password must be not blank.")
    String password,
    
    @NotBlank(message = "The name must be not blank.")
    String name,

    LocalDate birthDate,
    String documentNumber,
    String phoneNumber
) { }
