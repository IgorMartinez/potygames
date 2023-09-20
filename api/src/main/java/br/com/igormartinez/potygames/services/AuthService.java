package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.TokenCreationErrorException;
import br.com.igormartinez.potygames.mappers.UserToUserDTOMapper;
import br.com.igormartinez.potygames.exceptions.InvalidTokenException;
import br.com.igormartinez.potygames.exceptions.InvalidUsernamePasswordException;
import br.com.igormartinez.potygames.models.Permission;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.PermissionRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.jwt.JwtTokenProvider;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserToUserDTOMapper mapper;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordManager passwordManager;

    public AuthService(UserRepository userRepository, PermissionRepository permissionRepository,
            UserToUserDTOMapper mapper, JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager,
            PasswordManager passwordManager) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.mapper = mapper;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordManager = passwordManager;
    }

    /**
     * Signup a user
     * @param registrationDTO must be not null and already validated.
     * @return Object with the informations of user created.
     */
    public UserDTO signup(UserRegistrationDTO registrationDTO) {
        
        if (userRepository.existsByEmail(registrationDTO.email()))
            throw new ResourceAlreadyExistsException("The email is already in use.");

        User user = new User();
        user.setEmail(registrationDTO.email());
        user.setName(registrationDTO.name());
        user.setPassword(passwordManager.encodePassword(registrationDTO.password()));
        user.setBirthDate(registrationDTO.birthDate());
        user.setDocumentNumber(
            registrationDTO.documentNumber() != null && registrationDTO.documentNumber().isBlank() 
            ? null
            : registrationDTO.documentNumber());
        user.setPhoneNumber(
            registrationDTO.phoneNumber() != null && registrationDTO.phoneNumber().isBlank() 
            ? null
            : registrationDTO.phoneNumber());
        user.setAccountNonExpired(Boolean.TRUE);
        user.setAccountNonLocked(Boolean.TRUE);
        user.setCredentialsNonExpired(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);

        Permission permission = permissionRepository.findByDescription(PermissionType.CUSTOMER.getValue());
        user.setPermissions(List.of(permission));

        User createdUser = userRepository.save(user);
        return mapper.apply(createdUser);
    }

    public Token signin(AccountCredentials accountCredentials) {
        String username = accountCredentials.username();
        String password = accountCredentials.password();
        
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new InvalidUsernamePasswordException());
        
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return tokenProvider.createAccessToken(username, user.getPermissionDescriptionList());
        } catch (BadCredentialsException ex) {
            throw new InvalidUsernamePasswordException();
        } catch (JWTCreationException ex){
            throw new TokenCreationErrorException();
        }
    }

    public Token refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank())
            throw new RequestValidationException("The refresh token must be not blank.");
        
        try {
            return tokenProvider.refreshToken(refreshToken);
        } catch (JWTVerificationException ex){
            throw new InvalidTokenException("Invalid refresh token.");
        } catch (JWTCreationException ex){
            throw new TokenCreationErrorException();
        }
    }
}
