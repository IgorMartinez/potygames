package br.com.igormartinez.potygames.data.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserPersonalInformationDTO(
    @NotNull(message = "The id of user must be provided.")
    @Positive(message = "The id of user must be a positive number.")
    Long id,

    @NotBlank(message = "The name must be not blank.") 
    String name,
    
    LocalDate birthDate,
    String documentNumber,
    String phoneNumber
) {}
