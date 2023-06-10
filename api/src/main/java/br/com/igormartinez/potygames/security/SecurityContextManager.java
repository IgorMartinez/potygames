package br.com.igormartinez.potygames.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.models.User;

@Service
public class SecurityContextManager {
    
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
