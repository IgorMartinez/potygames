package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String ex) {
        super(ex);
    }

    public InvalidTokenException() {
        super("Invalid token");
    }
}
