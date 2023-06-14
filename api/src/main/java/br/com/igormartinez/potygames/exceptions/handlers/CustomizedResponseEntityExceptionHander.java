package br.com.igormartinez.potygames.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.exceptions.InvalidTokenException;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.exceptions.InvalidUsernamePasswordException;
import br.com.igormartinez.potygames.exceptions.MalformedRequestTokenException;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHander extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "Internal Server Error", 
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                ex.getMessage(), 
                request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "Not Found", 
                HttpStatus.NOT_FOUND.value(), 
                ex.getMessage(), 
                request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
        RequestObjectIsNullException.class, 
        InvalidUsernamePasswordException.class,
        MalformedRequestTokenException.class,
        InvalidTokenException.class, 
        BadCredentialsException.class, 
        UsernameNotFoundException.class})
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "Bad Request", 
                HttpStatus.BAD_REQUEST.value(), 
                ex.getMessage(), 
                request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public final ResponseEntity<ExceptionResponse> handleUnauthorizedExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "Unauthorized", 
                HttpStatus.UNAUTHORIZED.value(), 
                ex.getMessage(), 
                request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public final ResponseEntity<ExceptionResponse> handleConflictExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "Conflict", 
                HttpStatus.CONFLICT.value(), 
                ex.getMessage(), 
                request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }
}
