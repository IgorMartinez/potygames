package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MalformedRequestTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MalformedRequestTokenException(String ex) {
        super(ex);
    }

    public MalformedRequestTokenException() {
        super("Request token is not properly formatted");
    }
}
