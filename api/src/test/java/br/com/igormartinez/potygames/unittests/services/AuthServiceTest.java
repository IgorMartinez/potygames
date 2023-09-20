package br.com.igormartinez.potygames.unittests.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.InvalidTokenException;
import br.com.igormartinez.potygames.exceptions.InvalidUsernamePasswordException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.TokenCreationErrorException;
import br.com.igormartinez.potygames.mappers.UserToUserDTOMapper;
import br.com.igormartinez.potygames.mocks.MockToken;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.Permission;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.PermissionRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.jwt.JwtTokenProvider;
import br.com.igormartinez.potygames.services.AuthService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private AuthService service;
    private MockUser mockUser;
    private MockToken mockToken;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PasswordManager passwordManager;

    @BeforeEach
    void setUp() throws Exception {
        mockUser = new MockUser();
        mockToken = new MockToken();
        
        service = new AuthService(
            userRepository, 
            permissionRepository, 
            new UserToUserDTOMapper(), 
            tokenProvider, 
            authenticationManager, 
            passwordManager);
    }

    @Test
    public void testSignupWithExistingUser() {
        UserRegistrationDTO mockedUserRegistrationDTO = mockUser.mockUserRegistrationDTO(1);

        when(userRepository.existsByEmail(mockedUserRegistrationDTO.email())).thenReturn(Boolean.TRUE);

        Exception output = assertThrows(ResourceAlreadyExistsException.class, () -> {
            service.signup(mockedUserRegistrationDTO);
        });
        String expectedMessage = "The email is already in use.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    public void testSignupWithNotExistingUser() {
        UserRegistrationDTO mockedUserRegistrationDTO = mockUser.mockUserRegistrationDTO(1);
        User mockedUser = mockUser.mockUserSignup(1);

        Permission mockedPermission = new Permission();
        mockedPermission.setDescription(PermissionType.CUSTOMER.getValue());

        when(userRepository.existsByEmail(mockedUserRegistrationDTO.email())).thenReturn(Boolean.FALSE);
        when(passwordManager.encodePassword(mockedUserRegistrationDTO.password())).thenReturn("encodedPassword");
        when(permissionRepository.findByDescription(PermissionType.CUSTOMER.getValue())).thenReturn(mockedPermission);
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        UserDTO output = service.signup(mockedUserRegistrationDTO);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals("user_mail1@test.com", output.email());
        assertEquals("User name 1", output.name());
        assertEquals(LocalDate.of(1951, 2, 2), output.birthDate());
        assertEquals("000.000.000-01", output.documentNumber());
        assertEquals("+5500900000001", output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertTrue(output.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User capturedObject = argumentCaptor.getValue();
        assertNotNull(capturedObject);
        assertNull(capturedObject.getId());
        assertEquals("user_mail1@test.com", capturedObject.getEmail());
        assertEquals("User name 1", capturedObject.getName());
        assertEquals(LocalDate.of(1951, 2, 2), capturedObject.getBirthDate());
        assertEquals("000.000.000-01", capturedObject.getDocumentNumber());
        assertEquals("+5500900000001", capturedObject.getPhoneNumber());
        assertTrue(capturedObject.getAccountNonExpired());
        assertTrue(capturedObject.getAccountNonLocked());
        assertTrue(capturedObject.getCredentialsNonExpired());
        assertTrue(capturedObject.getEnabled());
        assertEquals(1, capturedObject.getPermissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), capturedObject.getPermissions().get(0).getDescription());
    }

    @Test
    public void testSignupWithNotExistingUserAndOptionalParamsNull() {
        UserRegistrationDTO mockedUserRegistrationDTO = new UserRegistrationDTO(
            "user_mail1@test.com", 
            "password1",
            "User name 1",
            null,
            null,
            null
        );
        User mockedUser = mockUser.mockUserSignup(1);
        mockedUser.setBirthDate(null);
        mockedUser.setDocumentNumber(null);
        mockedUser.setPhoneNumber(null);

        Permission mockedPermission = new Permission();
        mockedPermission.setDescription(PermissionType.CUSTOMER.getValue());

        when(userRepository.existsByEmail(mockedUserRegistrationDTO.email())).thenReturn(Boolean.FALSE);
        when(passwordManager.encodePassword(mockedUserRegistrationDTO.password())).thenReturn("encodedPassword");
        when(permissionRepository.findByDescription(PermissionType.CUSTOMER.getValue())).thenReturn(mockedPermission);
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        UserDTO output = service.signup(mockedUserRegistrationDTO);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals("user_mail1@test.com", output.email());
        assertEquals("User name 1", output.name());
        assertNull(output.birthDate());
        assertNull(output.documentNumber());
        assertNull(output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertTrue(output.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User capturedObject = argumentCaptor.getValue();
        assertNotNull(capturedObject);
        assertNull(capturedObject.getId());
        assertEquals("user_mail1@test.com", capturedObject.getEmail());
        assertEquals("User name 1", capturedObject.getName());
        assertEquals("encodedPassword", capturedObject.getPassword());
        assertNull(capturedObject.getBirthDate());
        assertNull(capturedObject.getDocumentNumber());
        assertNull(capturedObject.getPhoneNumber());
        assertTrue(capturedObject.getAccountNonExpired());
        assertTrue(capturedObject.getAccountNonLocked());
        assertTrue(capturedObject.getCredentialsNonExpired());
        assertTrue(capturedObject.getEnabled());
        assertEquals(1, capturedObject.getPermissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), capturedObject.getPermissions().get(0).getDescription());
    }

    @Test
    public void testSignupWithNotExistingUserAndOptionalParamsBlank() {
        UserRegistrationDTO mockedUserRegistrationDTO = new UserRegistrationDTO(
            "user_mail1@test.com", 
            "password1",
            "User name 1",
            null,
            " ",
            " "
        );
        User mockedUser = mockUser.mockUserSignup(1);
        mockedUser.setBirthDate(null);
        mockedUser.setDocumentNumber(null);
        mockedUser.setPhoneNumber(null);

        Permission mockedPermission = new Permission();
        mockedPermission.setDescription(PermissionType.CUSTOMER.getValue());

        when(userRepository.existsByEmail(mockedUserRegistrationDTO.email())).thenReturn(Boolean.FALSE);
        when(passwordManager.encodePassword(mockedUserRegistrationDTO.password())).thenReturn("encodedPassword");
        when(permissionRepository.findByDescription(PermissionType.CUSTOMER.getValue())).thenReturn(mockedPermission);
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        UserDTO output = service.signup(mockedUserRegistrationDTO);
        assertNotNull(output);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals("user_mail1@test.com", output.email());
        assertEquals("User name 1", output.name());
        assertNull(output.birthDate());
        assertNull(output.documentNumber());
        assertNull(output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertTrue(output.permissions().get(0).equals(PermissionType.CUSTOMER.getValue()));

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User capturedObject = argumentCaptor.getValue();
        assertNotNull(capturedObject);
        assertNull(capturedObject.getId());
        assertEquals("user_mail1@test.com", capturedObject.getEmail());
        assertEquals("User name 1", capturedObject.getName());
        assertEquals("encodedPassword", capturedObject.getPassword());
        assertNull(capturedObject.getBirthDate());
        assertNull(capturedObject.getDocumentNumber());
        assertNull(capturedObject.getPhoneNumber());
        assertTrue(capturedObject.getAccountNonExpired());
        assertTrue(capturedObject.getAccountNonLocked());
        assertTrue(capturedObject.getCredentialsNonExpired());
        assertTrue(capturedObject.getEnabled());
        assertEquals(1, capturedObject.getPermissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), capturedObject.getPermissions().get(0).getDescription());
    }
    
    @Test
    void testSigninWithUserNotFound() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");

        when(userRepository.findByEmail("test")).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(InvalidUsernamePasswordException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Invalid email or password.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithWrongPassword() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");
        User user = mockUser.mockUser(1);

        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        Exception output = assertThrows(InvalidUsernamePasswordException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Invalid email or password.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithTokenCreationError() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");
        User user = mockUser.mockUser(1);

        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(null);
        when(tokenProvider.createAccessToken("test", user.getPermissionDescriptionList()))
            .thenThrow(new JWTCreationException(null, null));

        Exception output = assertThrows(TokenCreationErrorException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "There was an error while creating the JWT token.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithTokenCreationSuccess() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");
        User user = mockUser.mockUser(1);
        Token token = mockToken.mockToken(accountCredentials.username());

        ZonedDateTime expectedCreatedTime = ZonedDateTime.of(
                2023, 
                06, 
                13, 
                13, 
                27, 
                0, 
                0, 
                ZoneId.systemDefault());

        ZonedDateTime expectedExpirationTime = ZonedDateTime.of(
                2023, 
                06, 
                13, 
                14, 
                27, 
                0, 
                0, 
                ZoneId.systemDefault());

        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(null);
        when(tokenProvider.createAccessToken("test", user.getPermissionDescriptionList()))
            .thenReturn(token);

        Token output = service.signin(accountCredentials);
        assertNotNull(output);
        assertEquals("test", output.getUsername());
        assertEquals(Boolean.TRUE, output.getAuthenticated());
        assertTrue(expectedCreatedTime.isEqual(output.getCreated()));
        assertTrue(expectedExpirationTime.isEqual(output.getExpiration()));
        assertEquals("mockedAccessToken", output.getAccessToken());
        assertEquals("mockedRefreshToken", output.getRefreshToken());
    }

    @Test
    void testRefreshWithParamNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.refresh(null);
        });
        String expectedMessage = "The refresh token must be not blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRefreshWithParamBlank() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.refresh("");
        });
        String expectedMessage = "The refresh token must be not blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRefreshWithInvalidToken() {
        when(tokenProvider.refreshToken("mockedRefreshToken"))
            .thenThrow(new JWTVerificationException(null));

        Exception output = assertThrows(InvalidTokenException.class, () -> {
            service.refresh("mockedRefreshToken");
        });
        String expectedMessage = "Invalid refresh token.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRefreshWithTokenCreationError() {
        when(tokenProvider.refreshToken("mockedRefreshToken"))
            .thenThrow(new JWTCreationException(null, null));

        Exception output = assertThrows(TokenCreationErrorException.class, () -> {
            service.refresh("mockedRefreshToken");
        });
        String expectedMessage = "There was an error while creating the JWT token.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRefreshWithTokenCreationSuccess() {
        Token token = mockToken.mockToken("test");

        ZonedDateTime expectedCreatedTime = ZonedDateTime.of(
                2023, 
                06, 
                13, 
                13, 
                27, 
                0, 
                0, 
                ZoneId.systemDefault());

        ZonedDateTime expectedExpirationTime = ZonedDateTime.of(
                2023, 
                06, 
                13, 
                14, 
                27, 
                0, 
                0, 
                ZoneId.systemDefault());
        
        when(tokenProvider.refreshToken("mockedRefreshToken")).thenReturn(token);

        Token output = service.refresh("mockedRefreshToken");
        assertNotNull(output);
        assertEquals("test", output.getUsername());
        assertEquals(Boolean.TRUE, output.getAuthenticated());
        assertTrue(expectedCreatedTime.isEqual(output.getCreated()));
        assertTrue(expectedExpirationTime.isEqual(output.getExpiration()));
        assertEquals("mockedAccessToken", output.getAccessToken());
        assertEquals("mockedRefreshToken", output.getRefreshToken());
    }

}
