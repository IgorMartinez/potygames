package br.com.igormartinez.potygames.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
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

    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(AccountCredentials accountCredentials) {
        
        if (isAccountCredentialsNull(accountCredentials))
            throw new BadCredentialsException("Invalid client request");
        
        String username = accountCredentials.getUsername();
        String password = accountCredentials.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        User user = repository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        
        Token token = tokenProvider.createAccessToken(username, user.getPermissionDescriptionList());
        if (token == null)
            throw new BadCredentialsException("Invalid client request");

        return ResponseEntity.ok(token);
    }
    
    private boolean isAccountCredentialsNull(AccountCredentials accountCredentials) {
        return accountCredentials == null 
            || accountCredentials.getUsername() == null || accountCredentials.getUsername().isBlank()
            || accountCredentials.getPassword() == null || accountCredentials.getPassword().isBlank();
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank())
            throw new BadCredentialsException("Invalid client request");
        
        Token token = tokenProvider.refreshToken(refreshToken);

        if (token == null)
            throw new BadCredentialsException("Invalid client request");

        return ResponseEntity.ok(token);
    }
}
