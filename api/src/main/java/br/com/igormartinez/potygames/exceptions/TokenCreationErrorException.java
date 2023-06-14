package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TokenCreationErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenCreationErrorException(String ex) {
        super(ex);
    }

    public TokenCreationErrorException() {
        super("There was an error while creating the JWT token");
    }
}
