package br.com.igormartinez.potygames.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.jwt.JwtTokenProvider;

@Service
public class AuthService {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(AccountCredentials accountCredentials) {
        
        if (isAccountCredentialsNull(accountCredentials))
            throw new BadCredentialsException("Invalid client request");
        
        String username = accountCredentials.getUsername();
        String password = accountCredentials.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        User user = repository.findByEmail(username);
        if (user == null)
            throw new UsernameNotFoundException("Username " + username + " not found");
        
        Token token = tokenProvider.createAccessToken(username, user.getRoles());
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

    public boolean verifyPermissionUserAuthenticated(PermissionType permission) {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(ga -> ga.getAuthority().equals(permission.getValue()));
    }

    public boolean verifyIdUserAuthenticated(long id) {
        User userAuthenticated = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return id == userAuthenticated.getId();
    }
}
