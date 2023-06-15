package br.com.igormartinez.potygames.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.models.User;

@Service
public class SecurityContextManager {

    private boolean verifyIdUserAuthenticated(long id) {
        User userAuthenticated = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return id == userAuthenticated.getId();
    }

    private boolean verifyPermissionUserAuthenticated(PermissionType permission) {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(ga -> ga.getAuthority().equals(permission.getValue()));
    }

    /**
     * Verify if the user of authorization token from request is ADMIN
     * @return boolean - true if user is admin
     */
    public boolean checkAdmin() {
        return verifyPermissionUserAuthenticated(PermissionType.ADMIN);
    }

    /**
     * Verify if the id of user of authorization token is the same of the param
     * @param id
     * @return boolean - if the id param is the same of token 
     */
    public boolean checkSameUser(long id) {
        return verifyIdUserAuthenticated(id);
    }

    /**
     * Verify the two setences: if the user of authorization token from request is ADMIN
     * OR if the id of user of authorization token is the same of the param
     * @param id
     * @return boolean - true if the user is admin OR if the id param is the same of token
     */
    public boolean checkSameUserOrAdmin(long id) {
        return checkSameUser(id) || checkAdmin();
    }
}
