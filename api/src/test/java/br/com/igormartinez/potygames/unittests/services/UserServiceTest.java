package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.igormartinez.potygames.data.request.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserToUserDTOMapper;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.PermissionRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.UserService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    private MockUser mockEntity;
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PasswordManager passwordManager;

    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setUp() throws Exception {
        mockEntity = new MockUser();
        
        service = new UserService(
            userRepository, 
            new UserToUserDTOMapper(), 
            securityContextManager);
    }

    @Test
    public void testLoadUserByUsernameWithNullParam() {
        when(userRepository.findByEmail(null)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(null);
        });
        String expectedMessage = "User not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testLoadUserByUsernameWithFindUser(){
        User mockedUser = mockEntity.mockUser(1);
        
        when(userRepository.findByEmail(mockedUser.getEmail())).thenReturn(Optional.of(mockedUser));

        UserDetails output = service.loadUserByUsername(mockedUser.getEmail());
        assertNotNull(output);
        assertNotNull(output.getUsername());
        assertNotNull(output.getPassword());
        assertEquals("user_mail1@test.com", output.getUsername());
        assertEquals("password1", output.getPassword());
        assertEquals(1, output.getAuthorities().size());
        assertTrue(output.getAuthorities()
            .stream().anyMatch(ga -> ga.getAuthority().equals(PermissionType.CUSTOMER.getValue()))
        );
    }

    @Test
    public void testLoadUserByUsernameWithNotFindUser() {
        when(userRepository.findByEmail("notfinduser@byemail")).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("notfinduser@byemail");
        });
        String expectedMessage = "User not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindAllAsAdmin() {
        List<User> mockedListUser = mockEntity.mockUserList(10);
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(userRepository.findAll()).thenReturn(mockedListUser);

        List<UserDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(10, output.size());

        UserDTO outputPos0 = output.get(0);
        assertEquals(Long.valueOf(1L), outputPos0.id());
        assertEquals("user_mail1@test.com", outputPos0.email());
        assertEquals("User name 1", outputPos0.name());
        assertEquals(LocalDate.of(1951, 2, 2), outputPos0.birthDate());
        assertEquals("000.000.000-01", outputPos0.documentNumber());
        assertEquals("+5500900000001", outputPos0.phoneNumber());
        assertFalse(outputPos0.accountNonExpired());
        assertFalse(outputPos0.accountNonLocked());
        assertFalse(outputPos0.credentialsNonExpired());
        assertFalse(outputPos0.enabled());
        assertEquals(1, outputPos0.permissions().size());
        assertTrue(outputPos0.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));

        UserDTO outputPos5 = output.get(5);
        assertEquals(Long.valueOf(6L), outputPos5.id());
        assertEquals("user_mail6@test.com", outputPos5.email());
        assertEquals("User name 6", outputPos5.name());
        assertEquals(LocalDate.of(1956, 7, 7), outputPos5.birthDate());
        assertEquals("000.000.000-06", outputPos5.documentNumber());
        assertEquals("+5500900000006", outputPos5.phoneNumber());
        assertTrue(outputPos5.accountNonExpired());
        assertTrue(outputPos5.accountNonLocked());
        assertTrue(outputPos5.credentialsNonExpired());
        assertTrue(outputPos5.enabled());
        assertEquals(1, outputPos5.permissions().size());
        assertTrue(outputPos5.permissions().get(0).equals(PermissionType.ADMIN.getValue()));

        UserDTO outputPos9 = output.get(9);
        assertEquals(Long.valueOf(10L), outputPos9.id());
        assertEquals("user_mail10@test.com", outputPos9.email());
        assertEquals("User name 10", outputPos9.name());
        assertEquals(LocalDate.of(1960, 11, 11), outputPos9.birthDate());
        assertEquals("000.000.000-10", outputPos9.documentNumber());
        assertEquals("+5500900000010", outputPos9.phoneNumber());
        assertTrue(outputPos9.accountNonExpired());
        assertTrue(outputPos9.accountNonLocked());
        assertTrue(outputPos9.credentialsNonExpired());
        assertTrue(outputPos9.enabled());
        assertEquals(1, outputPos9.permissions().size());
        assertTrue(outputPos9.permissions().get(0).equals(PermissionType.ADMIN.getValue()));
    }

    @Test
    public void testFindAllAsNotAdmin() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findAll();
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindAllWithNoUsersInDatabase() {
        List<User> mockedListUser = mockEntity.mockUserList(0);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(userRepository.findAll()).thenReturn(mockedListUser);

        List<UserDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    public void testFindByIdWithParamNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithParamZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithParamNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(-1231L);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithPermission() {
        User mockedUser = mockEntity.mockUser(1);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockedUser));

        UserDTO output = service.findById(1L);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals("user_mail1@test.com", output.email());
        assertEquals("User name 1", output.name());
        assertTrue(LocalDate.of(1951, 2, 2).isEqual(output.birthDate()));
        assertEquals("000.000.000-01", output.documentNumber());
        assertEquals("+5500900000001", output.phoneNumber());
        assertFalse(output.accountNonExpired());
        assertFalse(output.accountNonLocked());
        assertFalse(output.credentialsNonExpired());
        assertFalse(output.enabled());
        assertEquals(1, output.permissions().size());
        assertTrue(output.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));
    }

    @Test
    public void testFindByIdWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);
        
        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithNotFoundUser() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The user was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamIdNull() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.updatePersonaInformation(null, userDTO);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamIdZero() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.updatePersonaInformation(0L, userDTO);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamIdNegative() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.updatePersonaInformation(-555L, userDTO);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
    
    @Test
    public void testUpdatePersonalInformationWithMismatchParamIdAndDTO() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            2L, "User name 1",
            null,null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the user-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithPermission() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, "User name updated 1",
            LocalDate.of(2010, 6, 12),
            "000.000.001-01", "+5500987650001");
        User user = mockEntity.mockUser(1);
        
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO output = service.updatePersonaInformation(1L, userDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("User name updated 1", output.name());
        assertEquals(LocalDate.of(2010, 6, 12),output.birthDate());
        assertEquals("000.000.001-01", output.documentNumber());
        assertEquals("+5500987650001", output.phoneNumber());
    }

    @Test
    public void testUpdatePersonalInformationWithPermissionAndOptionalParamNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, "User name updated 1",
            null, null, null);
        User user = mockEntity.mockUser(1);
        
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO output = service.updatePersonaInformation(1L, userDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("User name updated 1", output.name());
        assertNull(output.birthDate());
        assertNull(output.documentNumber());
        assertNull(output.phoneNumber());
    }

    @Test
    public void testUpdatePersonalInformationWithPermissionAndOptionalParamBlank() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, "User name updated 1",
            null, " ", "");
        User user = mockEntity.mockUser(1);
        
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO output = service.updatePersonaInformation(1L, userDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("User name updated 1", output.name());
        assertNull(output.birthDate());
        assertNull(output.documentNumber());
        assertNull(output.phoneNumber());
    }

    @Test
    public void testUpdatePersonalInformationWithoutPermission() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);
        
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithNotFoundUser() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);
        
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "The user was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithParamNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithParamZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithParamNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(-1156L);
        });
        String expectedMessage = "The user-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.TRUE);

        service.delete(1L);
    }

    @Test
    public void testDeleteWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithNotFoundUser() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The user was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
}
