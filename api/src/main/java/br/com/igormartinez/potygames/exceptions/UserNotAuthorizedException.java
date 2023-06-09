package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotAuthorizedException(String ex) {
        super(ex);
    }

    public UserNotAuthorizedException() {
        super("The user is not authorized to access this resource");
    }
}
