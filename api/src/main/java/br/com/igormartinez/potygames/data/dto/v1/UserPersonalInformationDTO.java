package br.com.igormartinez.potygames.data.dto.v1;

import java.time.LocalDate;

public record UserPersonalInformationDTO(
    Long id,
    String name,
    LocalDate birthDate,
    String documentNumber
) {}
