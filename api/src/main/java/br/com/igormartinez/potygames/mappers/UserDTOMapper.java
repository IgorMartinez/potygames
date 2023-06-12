package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.models.User;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
            user.getId(),
            user.getEmail(), 
            user.getName(),
            user.getBirthDate(),
            user.getDocumentNumber(),
            user.getAccountNonExpired(), 
            user.isAccountNonLocked(), 
            user.getCredentialsNonExpired(), 
            user.getEnabled(), 
            user.getPermissionDescriptionList()
        );
    }
}
