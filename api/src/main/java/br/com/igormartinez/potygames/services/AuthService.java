package br.com.igormartinez.potygames.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.TokenCreationErrorException;
import br.com.igormartinez.potygames.exceptions.InvalidTokenException;
import br.com.igormartinez.potygames.exceptions.InvalidUsernamePasswordException;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.jwt.JwtTokenProvider;

@Service
public class AuthService {
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;

    public AuthService(JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager,
            UserRepository repository) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.repository = repository;
    }

    public Token signin(AccountCredentials accountCredentials) {
        
        if (accountCredentials == null 
            || accountCredentials.getUsername() == null || accountCredentials.getUsername().isBlank()
            || accountCredentials.getPassword() == null || accountCredentials.getPassword().isBlank())
            throw new RequestObjectIsNullException();
        
        String username = accountCredentials.getUsername();
        String password = accountCredentials.getPassword();
        
        User user = repository.findByEmail(username)
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
            throw new RequestObjectIsNullException();
        
        try {
            return tokenProvider.refreshToken(refreshToken);
        } catch (JWTVerificationException ex){
            throw new InvalidTokenException();
        } catch (JWTCreationException ex){
            throw new TokenCreationErrorException();
        }
    }
}
