package br.com.igormartinez.potygames.unittests.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.mocks.MockToken;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;
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

    @BeforeEach
    void setUp() throws Exception {
        mockUser = new MockUser();
        mockToken = new MockToken();
        
        service = new AuthService(tokenProvider, authenticationManager, userRepository);
    }

    @Test
    void testSigninWithParamNull() {
        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.signin(null);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithParamUsernameNull() {
        AccountCredentials accountCredentials = new AccountCredentials(null, "test");

        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithParamUsernameBlank() {
        AccountCredentials accountCredentials = new AccountCredentials("", "test");

        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithParamPasswordNull() {
        AccountCredentials accountCredentials = new AccountCredentials("test", null);

        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithParamPasswordBlank() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "");

        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithUserNotFound() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");

        when(userRepository.findByEmail("test")).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(UsernameNotFoundException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));

    }

    @Test
    void testSigninWithWrongPassword() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");
        User user = mockUser.mockUser(1);

        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        Exception output = assertThrows(AuthenticationException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithCorrectCredentials() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");
        User user = mockUser.mockUser(1);
        Token token = mockToken.mockToken(accountCredentials.getUsername());

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
    void testSigninWithGeneratedTokenNull() {
        AccountCredentials accountCredentials = new AccountCredentials("test", "test");
        User user = mockUser.mockUser(1);

        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(null);
        when(tokenProvider.createAccessToken("test", user.getPermissionDescriptionList()))
            .thenReturn(null);

        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.signin(accountCredentials);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRefreshWithParamNull() {
        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.refresh(null);
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRefreshWithParamBlank() {
        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.refresh("");
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testSigninWithCorrectToken() {
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

    @Test
    void testRefreshWithGeneratedTokenNull() {
        when(tokenProvider.refreshToken("mockedRefreshToken")).thenReturn(null);

        Exception output = assertThrows(BadCredentialsException.class, () -> {
            service.refresh("mockedRefreshToken");
        });
        String expectedMessage = "Bad credentials";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

}
