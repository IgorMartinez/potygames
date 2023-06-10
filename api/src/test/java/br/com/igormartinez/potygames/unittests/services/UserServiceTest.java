package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequiredObjectIsNullException;
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
    public void testLoadUserByUsername(){
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
    public void testLoadUserByUsernameWithNullParam() {
        when(userRepository.findByEmail(null)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(null);
        });
        String expectedMessage = "User not found";
        assertTrue(output.getMessage().contains(expectedMessage));
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
    public void testSignup() {
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
        assertEquals("User name 1", createdUserDTO.name());
        assertEquals("user_mail1@test.com", createdUserDTO.email());
        assertTrue(createdUserDTO.accountNonExpired());
        assertTrue(createdUserDTO.accountNonLocked());
        assertTrue(createdUserDTO.credentialsNonExpired());
        assertTrue(createdUserDTO.enabled());
        assertEquals(1, createdUserDTO.permissions().size());
        assertTrue(createdUserDTO.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));
    }

    @Test
    public void testSignupWithNullParam() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.signup(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNullName() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(null, "teste", "teste");

        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithBlankName() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(" ", "teste", "teste");

        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNullEmail() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("teste", null, "teste");

        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithBlankEmail() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("teste", "", "teste");

        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNullPassword() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("teste", "teste", null);

        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.signup(userRegistrationDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithBlankPassword() {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("teste", "teste", " ");

        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
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
        String expectedMessage = "User alrealdy exists";
        assertTrue(output.getMessage().contains(expectedMessage));
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
        assertEquals("User name 1", outputPos0.name());
        assertEquals("user_mail1@test.com", outputPos0.email());
        assertFalse(outputPos0.accountNonExpired());
        assertFalse(outputPos0.accountNonLocked());
        assertFalse(outputPos0.credentialsNonExpired());
        assertFalse(outputPos0.enabled());
        assertEquals(1, outputPos0.permissions().size());
        assertTrue(outputPos0.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));

        UserDTO outputPos5 = output.get(5);
        assertEquals(Long.valueOf(6L), outputPos5.id());
        assertEquals("User name 6", outputPos5.name());
        assertEquals("user_mail6@test.com", outputPos5.email());
        assertTrue(outputPos5.accountNonExpired());
        assertTrue(outputPos5.accountNonLocked());
        assertTrue(outputPos5.credentialsNonExpired());
        assertTrue(outputPos5.enabled());
        assertEquals(1, outputPos5.permissions().size());
        assertTrue(outputPos5.permissions().get(0).equals(PermissionType.ADMIN.getValue()));

        UserDTO outputPos9 = output.get(9);
        assertEquals(Long.valueOf(10L), outputPos9.id());
        assertEquals("User name 10", outputPos9.name());
        assertEquals("user_mail10@test.com", outputPos9.email());
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
        String expectedMessage = "The user not have permission to this resource";
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
    public void testFindByIdWithNullParam() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "ID cannot be null or less than zero";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithZeroParam() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "ID cannot be null or less than zero";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithNegativeParam() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.findById(-1231L);
        });
        String expectedMessage = "ID cannot be null or less than zero";
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
        assertEquals("User name 1", output.name());
        assertEquals("user_mail1@test.com", output.email());
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
        assertEquals("User name 1", output.name());
        assertEquals("user_mail1@test.com", output.email());
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
        String expectedMessage = "The user not have permission to this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testFindByIdWithNotFoundUser() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "User not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testUpdate() {
        // TODO: Escrever tests do UPDATE

        /*UserDTO mockedUserDTO = mockEntity.mockUserDTO(1);
        User mockedUser = mockEntity.mockUser(1);
        
        when(securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(mockedUserDTO.getEmail())).thenReturn(mockedUser);
        when(passwordManager.encodePassword(mockedUserDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        UserDTO output = service.update(mockedUserDTO);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.getId());
        assertEquals("User name 1", output.getName());
        assertEquals("user_mail1@test.com", output.getEmail());
        assertEquals("", output.getPassword());
        assertFalse(output.getAccountNonExpired());
        assertFalse(output.getAccountNonLocked());
        assertFalse(output.getCredentialsNonExpired());
        assertFalse(output.getEnabled());*/
    }

    @Test
    public void testDeleteWithNullId() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "ID cannot be null or less than zero";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithZeroId() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "ID cannot be null or less than zero";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithNegativeId() {
        Exception output = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.delete(-1156L);
        });
        String expectedMessage = "ID cannot be null or less than zero";
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
        String expectedMessage = "The user not have permission to this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDeleteWithNotFoundUser() {
        when(securityContextManager.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.existsById(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "User not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
}
