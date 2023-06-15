package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
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

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserDTOMapper;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.Permission;
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
            new UserDTOMapper(), 
            permissionRepository, 
            passwordManager, 
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
    public void testSignupWithParamNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithEmailNull() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                null, "pasword", "name", 
                LocalDate.of(2023,06,12), "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithEmailBlank() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                " ", "pasword", "name", 
                LocalDate.of(2023,06,12), "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithPasswordNull() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", null, "name", 
                LocalDate.of(2023,06,12), "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithPasswordBlank() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", "", "name", 
                LocalDate.of(2023,06,12), "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNameNull() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", "password", null, 
                LocalDate.of(2023,06,12), "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNameBlank() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", "password", "", 
                LocalDate.of(2023,06,12), "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithBirthDateNull() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", "password", "name", 
                null, "documentNumber");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithDocumentNumberNull() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", "password", "name", 
                LocalDate.of(2023,06,12), null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithDocumentNumberBlank() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                "email", "password", "", 
                LocalDate.of(2023,06,12), "");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithExistingUser() {
        UserRegistrationDTO mockedUserRegistrationDTO = mockEntity.mockUserRegistrationDTO(1);

        when(userRepository.existsByEmail(mockedUserRegistrationDTO.email())).thenReturn(Boolean.TRUE);

        Exception output = assertThrows(ResourceAlreadyExistsException.class, () -> {
            service.signup(mockedUserRegistrationDTO);
        });
        String expectedMessage = "Request could not be processed because the resource already exists";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNotExistingUser() {
        UserRegistrationDTO mockedUserRegistrationDTO = mockEntity.mockUserRegistrationDTO(1);
        User mockedUser = mockEntity.mockUserSignup(1);

        Permission mockedPermission = new Permission();
        mockedPermission.setDescription(PermissionType.CUSTOMER.getValue());

        when(userRepository.existsByEmail(mockedUserRegistrationDTO.email())).thenReturn(Boolean.FALSE);
        when(passwordManager.encodePassword(mockedUserRegistrationDTO.password())).thenReturn("encodedPassword");
        when(permissionRepository.findByDescription(PermissionType.CUSTOMER.getValue())).thenReturn(mockedPermission);
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        UserDTO createdUserDTO = service.signup(mockedUserRegistrationDTO);
        assertNotNull(createdUserDTO);
        assertEquals(Long.valueOf(1L), createdUserDTO.id());
        assertEquals("user_mail1@test.com", createdUserDTO.email());
        assertEquals("User name 1", createdUserDTO.name());
        assertTrue(LocalDate.of(1951, 2, 2).isEqual(createdUserDTO.birthDate()));
        assertEquals("000.000.000-01", createdUserDTO.documentNumber());
        assertTrue(createdUserDTO.accountNonExpired());
        assertTrue(createdUserDTO.accountNonLocked());
        assertTrue(createdUserDTO.credentialsNonExpired());
        assertTrue(createdUserDTO.enabled());
        assertEquals(1, createdUserDTO.permissions().size());
        assertTrue(createdUserDTO.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));
    }

    @Test
    public void testFindAllAsAdmin() {
        List<User> mockedListUser = mockEntity.mockUserList(10);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(userRepository.findAll()).thenReturn(mockedListUser);

        List<UserDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(10, output.size());

        UserDTO outputPos0 = output.get(0);
        assertEquals(Long.valueOf(1L), outputPos0.id());
        assertEquals("user_mail1@test.com", outputPos0.email());
        assertEquals("User name 1", outputPos0.name());
        assertTrue(LocalDate.of(1951, 2, 2).isEqual(outputPos0.birthDate()));
        assertEquals("000.000.000-01", outputPos0.documentNumber());
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
        assertTrue(LocalDate.of(1956, 7, 7).isEqual(outputPos5.birthDate()));
        assertEquals("000.000.000-06", outputPos5.documentNumber());
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
        assertTrue(LocalDate.of(1960, 11, 11).isEqual(outputPos9.birthDate()));
        assertEquals("000.000.000-10", outputPos9.documentNumber());
        assertTrue(outputPos9.accountNonExpired());
        assertTrue(outputPos9.accountNonLocked());
        assertTrue(outputPos9.credentialsNonExpired());
        assertTrue(outputPos9.enabled());
        assertEquals(1, outputPos9.permissions().size());
        assertTrue(outputPos9.permissions().get(0).equals(PermissionType.ADMIN.getValue()));
    }

    @Test
    public void testFindAllAsNotAdmin() {
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findAll();
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindAllWithNoUsersInDatabase() {
        List<User> mockedListUser = mockEntity.mockUserList(0);

        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(userRepository.findAll()).thenReturn(mockedListUser);

        List<UserDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    public void testFindByIdWithParamNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithParamZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithParamNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(-1231L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdAsSameUser() {
        User mockedUser = mockEntity.mockUser(1);

        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockedUser));

        UserDTO output = service.findById(1L);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals("user_mail1@test.com", output.email());
        assertEquals("User name 1", output.name());
        assertTrue(LocalDate.of(1951, 2, 2).isEqual(output.birthDate()));
        assertEquals("000.000.000-01", output.documentNumber());
        assertFalse(output.accountNonExpired());
        assertFalse(output.accountNonLocked());
        assertFalse(output.credentialsNonExpired());
        assertFalse(output.enabled());
        assertEquals(1, output.permissions().size());
        assertTrue(output.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));
    }

    @Test
    public void testFindByIdAsDifferenteUserAndAdmin() {
        User mockedUser = mockEntity.mockUser(1);

        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockedUser));

        UserDTO output = service.findById(1L);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals("user_mail1@test.com", output.email());
        assertEquals("User name 1", output.name());
        assertTrue(LocalDate.of(1951, 2, 2).isEqual(output.birthDate()));
        assertEquals("000.000.000-01", output.documentNumber());
        assertFalse(output.accountNonExpired());
        assertFalse(output.accountNonLocked());
        assertFalse(output.credentialsNonExpired());
        assertFalse(output.enabled());
        assertEquals(1, output.permissions().size());
        assertTrue(output.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));
    }

    @Test
    public void testFindByIdAsDifferentUserAndNotAdmin() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.FALSE);
        
        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithNotFoundUser() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamIdNull() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(null, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamIdZero() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(0L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamIdNegative() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(-555L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTONull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTOIdNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            null, 
            "User name 1",
            LocalDate.of(1951, 2, 2),
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTOIdZero() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            0L, 
            "User name 1",
            LocalDate.of(1951, 2, 2),
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTOIdNegative() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            -1231L, 
            "User name 1",
            LocalDate.of(1951, 2, 2),
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTOIdDifferentFromIdParam() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            2L, 
            "User name 1",
            LocalDate.of(1951, 2, 2),
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTONameNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            null,
            LocalDate.of(1951, 2, 2),
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTONameBlank() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            "",
            LocalDate.of(1951, 2, 2),
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTOBirthDateNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            "User name 1",
            null,
            "000.000.000-01"
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTODocumentNumberNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            "User name 1",
            LocalDate.of(1951, 2, 2),
            null
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithParamUserDTODocumentNumberBlank() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            "User name 1",
            LocalDate.of(1951, 2, 2),
            ""
        );

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationAsSameUser() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            "User name updated 1",
            LocalDate.of(2023, 06, 12),
            "001.001.001-01"
        );
        User user = mockEntity.mockUser(1);
        
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserPersonalInformationDTO output = service.updatePersonaInformation(1L, userDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("User name updated 1", output.name());
        assertTrue(LocalDate.of(2023, 06, 12).isEqual(output.birthDate()));
        assertEquals("001.001.001-01", output.documentNumber());
    }

    @Test
    public void testUpdatePersonalInformationAsDifferentUserAndAdmin() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, 
            "User name updated 1",
            LocalDate.of(2023, 06, 12),
            "001.001.001-01"
        );
        User user = mockEntity.mockUser(1);
        
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserPersonalInformationDTO output = service.updatePersonaInformation(1L, userDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("User name updated 1", output.name());
        assertTrue(LocalDate.of(2023, 06, 12).isEqual(output.birthDate()));
        assertEquals("001.001.001-01", output.documentNumber());
    }

    @Test
    public void testUpdatePersonalInformationAsDifferentUserAndNotAdmin() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);
        
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdatePersonalInformationWithNotFoundUser() {
        UserPersonalInformationDTO userDTO = mockEntity.mockUserPersonalInformationDTO(1);
        
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.updatePersonaInformation(1L, userDTO);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithParamNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithParamZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithParamNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(-1156L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteAsSameUser() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.TRUE);

        service.delete(1L);
    }

    @Test
    public void testDeleteAsDifferentUserAndAdmin() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        
        service.delete(1L);
    }

    @Test
    public void testDeleteAsDifferentUserAndNotAdmin() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.FALSE);
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithNotFoundUser() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
}
