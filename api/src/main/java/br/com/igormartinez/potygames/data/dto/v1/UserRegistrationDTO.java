package br.com.igormartinez.potygames.data.dto.v1;

import java.time.LocalDate;

public record UserRegistrationDTO (
    String email,
    String password,
    String name,
    LocalDate birthDate,
    String documentNumber,
    String phoneNumber
) { }
