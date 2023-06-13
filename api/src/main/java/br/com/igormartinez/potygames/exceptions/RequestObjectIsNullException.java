package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestObjectIsNullException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestObjectIsNullException(String ex) {
        super(ex);
    }

    public RequestObjectIsNullException() {
        super("Request object cannot be null");
    }
}
