package br.com.igormartinez.potygames.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DeleteAssociationConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeleteAssociationConflictException(String ex) {
        super(ex);
    }

    public DeleteAssociationConflictException() {
        super("Resource cannot be removed due to being associated with other resources");
    }
}
