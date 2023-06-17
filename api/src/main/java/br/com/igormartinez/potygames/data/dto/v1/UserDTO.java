package br.com.igormartinez.potygames.data.dto.v1;

import java.time.LocalDate;
import java.util.List;

public record UserDTO (
    Long id,
    String email,
    String name,
    LocalDate birthDate,
    String documentNumber,
    String phoneNumber,
    Boolean accountNonExpired,
    Boolean accountNonLocked, 
    Boolean credentialsNonExpired,
    Boolean enabled,
    List<String> permissions
) { }
