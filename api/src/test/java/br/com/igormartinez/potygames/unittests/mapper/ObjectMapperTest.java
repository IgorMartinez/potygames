package br.com.igormartinez.potygames.unittests.mapper;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.mapper.ObjectMapper;
import br.com.igormartinez.potygames.mock.MockUser;
import br.com.igormartinez.potygames.models.User;

public class ObjectMapperTest {
    
    MockUser mock;

    @BeforeEach
    public void setup() {
        mock = new MockUser();
    }

    @Test
    public void parseUserToUserDTO() {
        UserDTO output = ObjectMapper.parseObject(mock.mockUser(1), UserDTO.class);
        assertEquals(Long.valueOf(1L), output.getId());
        assertEquals("User name 1", output.getName());
        assertEquals("user_mail1@test.com", output.getEmail());
        assertEquals("password1", output.getPassword());
        assertFalse(output.getAccountNonExpired());
        assertFalse(output.getAccountNonLocked());
        assertFalse(output.getCredentialsNonExpired());
        assertFalse(output.getEnabled());
    }

    @Test
    public void parseUserListToUserDTOList() {
        List<UserDTO> outputList = ObjectMapper.parseListObjects(mock.mockUserList(10), UserDTO.class);
        
        UserDTO outputPos0 = outputList.get(0);
        assertEquals(Long.valueOf(1L), outputPos0.getId());
        assertEquals("User name 1", outputPos0.getName());
        assertEquals("user_mail1@test.com", outputPos0.getEmail());
        assertEquals("password1", outputPos0.getPassword());
        assertFalse(outputPos0.getAccountNonExpired());
        assertFalse(outputPos0.getAccountNonLocked());
        assertFalse(outputPos0.getCredentialsNonExpired());
        assertFalse(outputPos0.getEnabled());

        UserDTO outputPos5 = outputList.get(5);
        assertEquals(Long.valueOf(6L), outputPos5.getId());
        assertEquals("User name 6", outputPos5.getName());
        assertEquals("user_mail6@test.com", outputPos5.getEmail());
        assertEquals("password6", outputPos5.getPassword());
        assertTrue(outputPos5.getAccountNonExpired());
        assertTrue(outputPos5.getAccountNonLocked());
        assertTrue(outputPos5.getCredentialsNonExpired());
        assertTrue(outputPos5.getEnabled());

        UserDTO outputPos9 = outputList.get(9);
        assertEquals(Long.valueOf(10L), outputPos9.getId());
        assertEquals("User name 10", outputPos9.getName());
        assertEquals("user_mail10@test.com", outputPos9.getEmail());
        assertEquals("password10", outputPos9.getPassword());
        assertTrue(outputPos9.getAccountNonExpired());
        assertTrue(outputPos9.getAccountNonLocked());
        assertTrue(outputPos9.getCredentialsNonExpired());
        assertTrue(outputPos9.getEnabled());
    }

    @Test
    public void parseUserDTOToUser() {
        User output = ObjectMapper.parseObject(mock.mockUserDTO(1), User.class);
        assertEquals(Long.valueOf(1L), output.getId());
        assertEquals("User name 1", output.getName());
        assertEquals("user_mail1@test.com", output.getEmail());
        assertEquals("password1", output.getPassword());
        assertFalse(output.getAccountNonExpired());
        assertFalse(output.getAccountNonLocked());
        assertFalse(output.getCredentialsNonExpired());
        assertFalse(output.getEnabled());
        assertNull(output.getPermissions());
    }

    @Test
    public void parseUserDTOListToUserList() {
        List<User> outputList = ObjectMapper.parseListObjects(mock.mockUserDTOList(10), User.class);
        
        User outputPos0 = outputList.get(0);
        assertEquals(Long.valueOf(1L), outputPos0.getId());
        assertEquals("User name 1", outputPos0.getName());
        assertEquals("user_mail1@test.com", outputPos0.getEmail());
        assertEquals("password1", outputPos0.getPassword());
        assertFalse(outputPos0.getAccountNonExpired());
        assertFalse(outputPos0.getAccountNonLocked());
        assertFalse(outputPos0.getCredentialsNonExpired());
        assertFalse(outputPos0.getEnabled());
        assertNull(outputPos0.getPermissions());

        User outputPos5 = outputList.get(5);
        assertEquals(Long.valueOf(6L), outputPos5.getId());
        assertEquals("User name 6", outputPos5.getName());
        assertEquals("user_mail6@test.com", outputPos5.getEmail());
        assertEquals("password6", outputPos5.getPassword());
        assertTrue(outputPos5.getAccountNonExpired());
        assertTrue(outputPos5.getAccountNonLocked());
        assertTrue(outputPos5.getCredentialsNonExpired());
        assertTrue(outputPos5.getEnabled());
        assertNull(outputPos5.getPermissions());

        User outputPos9 = outputList.get(9);
        assertEquals(Long.valueOf(10L), outputPos9.getId());
        assertEquals("User name 10", outputPos9.getName());
        assertEquals("user_mail10@test.com", outputPos9.getEmail());
        assertEquals("password10", outputPos9.getPassword());
        assertTrue(outputPos9.getAccountNonExpired());
        assertTrue(outputPos9.getAccountNonLocked());
        assertTrue(outputPos9.getCredentialsNonExpired());
        assertTrue(outputPos9.getEnabled());
        assertNull(outputPos9.getPermissions());
    }

    @Test
    public void parseUserToUserRegistrationDTO() {
        UserRegistrationDTO output = ObjectMapper.parseObject(mock.mockUser(1), UserRegistrationDTO.class);
        assertEquals("User name 1", output.getName());
        assertEquals("user_mail1@test.com", output.getEmail());
        assertEquals("password1", output.getPassword());
    }

    @Test
    public void parseUserListToUserRegistrationDTOList() {
        List<UserRegistrationDTO> outputList = ObjectMapper.parseListObjects(mock.mockUserList(10), UserRegistrationDTO.class);
        
        UserRegistrationDTO outputPos0 = outputList.get(0);
        assertEquals("User name 1", outputPos0.getName());
        assertEquals("user_mail1@test.com", outputPos0.getEmail());
        assertEquals("password1", outputPos0.getPassword());

        UserRegistrationDTO outputPos5 = outputList.get(5);
        assertEquals("User name 6", outputPos5.getName());
        assertEquals("user_mail6@test.com", outputPos5.getEmail());
        assertEquals("password6", outputPos5.getPassword());

        UserRegistrationDTO outputPos9 = outputList.get(9);
        assertEquals("User name 10", outputPos9.getName());
        assertEquals("user_mail10@test.com", outputPos9.getEmail());
        assertEquals("password10", outputPos9.getPassword());
    }

    @Test
    public void parseUserRegistrationDTOToUser() {
        User output = ObjectMapper.parseObject(mock.mockUserRegistrationDTO(1), User.class);
        assertNull(output.getId());
        assertEquals("User name 1", output.getName());
        assertEquals("user_mail1@test.com", output.getEmail());
        assertEquals("password1", output.getPassword());
        assertNull(output.getAccountNonExpired());
        assertNull(output.getAccountNonLocked());
        assertNull(output.getCredentialsNonExpired());
        assertNull(output.getEnabled());
        assertNull(output.getPermissions());
    }

    @Test
    public void parseUserRegistrationDTOListToUserList() {
        List<User> outputList = ObjectMapper.parseListObjects(mock.mockUserRegistrationDTOList(10), User.class);
        
        User outputPos0 = outputList.get(0);
        assertNull(outputPos0.getId());
        assertEquals("User name 1", outputPos0.getName());
        assertEquals("user_mail1@test.com", outputPos0.getEmail());
        assertEquals("password1", outputPos0.getPassword());
        assertNull(outputPos0.getAccountNonExpired());
        assertNull(outputPos0.getAccountNonLocked());
        assertNull(outputPos0.getCredentialsNonExpired());
        assertNull(outputPos0.getEnabled());
        assertNull(outputPos0.getPermissions());

        User outputPos5 = outputList.get(5);
        assertNull(outputPos5.getId());
        assertEquals("User name 6", outputPos5.getName());
        assertEquals("user_mail6@test.com", outputPos5.getEmail());
        assertEquals("password6", outputPos5.getPassword());
        assertNull(outputPos5.getAccountNonExpired());
        assertNull(outputPos5.getAccountNonLocked());
        assertNull(outputPos5.getCredentialsNonExpired());
        assertNull(outputPos5.getEnabled());
        assertNull(outputPos5.getPermissions());

        User outputPos9 = outputList.get(9);
        assertNull(outputPos9.getId());
        assertEquals("User name 10", outputPos9.getName());
        assertEquals("user_mail10@test.com", outputPos9.getEmail());
        assertEquals("password10", outputPos9.getPassword());
        assertNull(outputPos9.getAccountNonExpired());
        assertNull(outputPos9.getAccountNonLocked());
        assertNull(outputPos9.getCredentialsNonExpired());
        assertNull(outputPos9.getEnabled());
        assertNull(outputPos9.getPermissions());
    }

}
