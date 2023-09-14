package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInsufficientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceInsufficientException(String ex) {
        super(ex);
    }

    public ResourceInsufficientException() {
        super("Request could not be processed because the resource is insufficient to complete the operation.");
    }
}
