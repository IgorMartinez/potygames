package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestValidationException(String ex) {
        super(ex);
    }

    public RequestValidationException() {
        super("Request data validation failed.");
    }
}
