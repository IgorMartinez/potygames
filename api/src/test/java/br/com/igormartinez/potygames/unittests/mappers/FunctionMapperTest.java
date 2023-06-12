package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.mappers.UserDTOMapper;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.User;

public class FunctionMapperTest {
    
    MockUser mockUser;

    UserDTOMapper userDTOMapper;

    @BeforeEach
    public void setup() {
        mockUser = new MockUser();
        userDTOMapper = new UserDTOMapper();
    }

    @Test
    public void testUserDTOMapper() {
        User user = mockUser.mockUser(1);
                
        UserDTO userDTO = userDTOMapper.apply(user);
        
        assertEquals(user.getId(), userDTO.id());
        assertEquals(user.getEmail(), userDTO.email());
        assertEquals(user.getName(), userDTO.name());
        assertTrue(user.getBirthDate().isEqual(userDTO.birthDate()));
        assertEquals(user.getDocumentNumber(), userDTO.documentNumber());
        assertEquals(user.getAccountNonExpired(), userDTO.accountNonExpired());
        assertEquals(user.getAccountNonLocked(), userDTO.accountNonLocked());
        assertEquals(user.getCredentialsNonExpired(), userDTO.credentialsNonExpired());
        assertEquals(user.getEnabled(), userDTO.enabled());
        assertEquals(user.getPermissionDescriptionList().size(), userDTO.permissions().size());
        assertTrue(user.getPermissionDescriptionList().containsAll(userDTO.permissions()));
    }
}
