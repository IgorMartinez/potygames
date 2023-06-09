package br.com.igormartinez.potygames.unittests.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.services.AuthService;
import br.com.igormartinez.potygames.services.UserService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    MockUser mockUser;

    @InjectMocks
    private UserService service;

    @Mock
    AuthService authService;

    @Mock
    UserRepository repository;

    @Mock
    PasswordManager passwordManager;

    @BeforeEach
    void setUp() throws Exception {
        mockUser = new MockUser();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername(){
        User user = mockUser.mockUser(1);
        when(repository.findByEmail("")).thenReturn(user);

        UserDetails searchedUser = service.loadUserByUsername("");
        assertNotNull(searchedUser);
        assertNotNull(searchedUser.getUsername());
        assertNotNull(searchedUser.getPassword());
        assertEquals("user_mail1@test.com", searchedUser.getUsername());
        assertEquals("password1", searchedUser.getPassword());
        assertEquals(1, searchedUser.getAuthorities().size());
        assertTrue(searchedUser.getAuthorities()
            .stream().anyMatch(ga -> ga.getAuthority().equals(PermissionType.CUSTOMER.getValue()))
        );
    }

    @Test
    public void testSignup() {
        UserRegistrationDTO mockedUserRegistrationDTO = mockUser.mockUserRegistrationDTO(1);
        User mockedUser = mockUser.mockUser(1);

        when(repository.findByEmail(mockedUserRegistrationDTO.getEmail())).thenReturn(null);
        when(passwordManager.encodePassword(mockedUserRegistrationDTO.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(mockedUser);

        UserRegistrationDTO createdUserRegistrationDTO = service.signup(mockedUserRegistrationDTO);
        assertNotNull(createdUserRegistrationDTO);
        assertEquals("User name 1", createdUserRegistrationDTO.getName());
        assertEquals("user_mail1@test.com", createdUserRegistrationDTO.getEmail());
        assertEquals("", createdUserRegistrationDTO.getPassword());
    }

    @Test
    public void testFindAll() {
        List<User> mockedListUser = mockUser.mockUserList(10);
        when(authService.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(repository.findAll()).thenReturn(mockedListUser);

        List<UserDTO> listUserDTO = service.findAll();
        assertNotNull(listUserDTO);
        assertEquals(10, listUserDTO.size());

        UserDTO outputPos0 = listUserDTO.get(0);
        assertEquals(Long.valueOf(1L), outputPos0.getId());
        assertEquals("User name 1", outputPos0.getName());
        assertEquals("user_mail1@test.com", outputPos0.getEmail());
        assertEquals("password1", outputPos0.getPassword());
        assertFalse(outputPos0.getAccountNonExpired());
        assertFalse(outputPos0.getAccountNonLocked());
        assertFalse(outputPos0.getCredentialsNonExpired());
        assertFalse(outputPos0.getEnabled());

        UserDTO outputPos5 = listUserDTO.get(5);
        assertEquals(Long.valueOf(6L), outputPos5.getId());
        assertEquals("User name 6", outputPos5.getName());
        assertEquals("user_mail6@test.com", outputPos5.getEmail());
        assertEquals("password6", outputPos5.getPassword());
        assertTrue(outputPos5.getAccountNonExpired());
        assertTrue(outputPos5.getAccountNonLocked());
        assertTrue(outputPos5.getCredentialsNonExpired());
        assertTrue(outputPos5.getEnabled());

        UserDTO outputPos9 = listUserDTO.get(9);
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
    public void testFindById() {
        User mockedUser = mockUser.mockUser(1);

        when(authService.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(authService.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.of(mockedUser));

        UserDTO userDTO = service.findById(1L);
        assertNotNull(userDTO);
        assertEquals(Long.valueOf(1L), userDTO.getId());
        assertEquals("User name 1", userDTO.getName());
        assertEquals("user_mail1@test.com", userDTO.getEmail());
        assertEquals("password1", userDTO.getPassword());
        assertFalse(userDTO.getAccountNonExpired());
        assertFalse(userDTO.getAccountNonLocked());
        assertFalse(userDTO.getCredentialsNonExpired());
        assertFalse(userDTO.getEnabled());
    }

    @Test
    public void testUpdate() {
        UserDTO mockedUserDTO = mockUser.mockUserDTO(1);
        User mockedUser = mockUser.mockUser(1);
        
        when(authService.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(authService.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(repository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByEmail(mockedUserDTO.getEmail())).thenReturn(mockedUser);
        when(passwordManager.encodePassword(mockedUserDTO.getPassword())).thenReturn("encodedPassword");
        when(repository.save(mockedUser)).thenReturn(mockedUser);

        UserDTO output = service.update(mockedUserDTO);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.getId());
        assertEquals("User name 1", output.getName());
        assertEquals("user_mail1@test.com", output.getEmail());
        assertEquals("", output.getPassword());
        assertFalse(output.getAccountNonExpired());
        assertFalse(output.getAccountNonLocked());
        assertFalse(output.getCredentialsNonExpired());
        assertFalse(output.getEnabled());
    }

    public void testDelete() {
        when(authService.verifyPermissionUserAuthenticated(PermissionType.ADMIN)).thenReturn(Boolean.TRUE);
        when(authService.verifyIdUserAuthenticated(1L)).thenReturn(Boolean.TRUE);
        when(repository.existsById(1L)).thenReturn(Boolean.TRUE);
        service.delete(1L);
    }
}
