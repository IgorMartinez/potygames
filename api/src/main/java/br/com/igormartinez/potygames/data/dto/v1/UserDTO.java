package br.com.igormartinez.potygames.data.dto.v1;

import java.util.List;

public record UserDTO (
    Long id,
    String name,
    String email,
    Boolean accountNonExpired,
    Boolean accountNonLocked, 
    Boolean credentialsNonExpired,
    Boolean enabled,
    List<String> permissions
) { }
