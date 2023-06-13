package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ErrorCreationTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ErrorCreationTokenException(String ex) {
        super(ex);
    }

    public ErrorCreationTokenException() {
        super("There was an error creating the token");
    }
}
