package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUsernamePasswordException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidUsernamePasswordException(String ex) {
        super(ex);
    }

    public InvalidUsernamePasswordException() {
        super("Invalid email or password");
    }
}
