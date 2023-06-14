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
import br.com.igormartinez.potygames.exceptions.TokenCreationErrorException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.exceptions.InvalidUsernamePasswordException;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHander extends ResponseEntityExceptionHandler {

    // Example:
    // request.getDescription(false) = uri=/auth/signin
    // request.getDescription(false).substring(SUBSTRING_URI) = /auth/signin
    private final int SUBSTRING_URI = 4;

    @ExceptionHandler({TokenCreationErrorException.class, Exception.class})
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "about:blank",
                "Internal Server Error", 
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                ex.getMessage(),
                request.getDescription(false).substring(SUBSTRING_URI));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "about:blank",
                "Not Found", 
                HttpStatus.NOT_FOUND.value(), 
                ex.getMessage(), 
                request.getDescription(false).substring(SUBSTRING_URI));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
        RequestObjectIsNullException.class,
        BadCredentialsException.class, 
        UsernameNotFoundException.class})
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "about:blank",
                "Bad Request", 
                HttpStatus.BAD_REQUEST.value(), 
                ex.getMessage(), 
                request.getDescription(false).substring(SUBSTRING_URI));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
        UserUnauthorizedException.class,
        InvalidUsernamePasswordException.class,
        InvalidTokenException.class})
    public final ResponseEntity<ExceptionResponse> handleUnauthorizedExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "about:blank",
                "Unauthorized", 
                HttpStatus.UNAUTHORIZED.value(), 
                ex.getMessage(), 
                request.getDescription(false).substring(SUBSTRING_URI));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public final ResponseEntity<ExceptionResponse> handleConflictExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = 
            new ExceptionResponse(
                "about:blank",
                "Conflict", 
                HttpStatus.CONFLICT.value(), 
                ex.getMessage(), 
                request.getDescription(false).substring(SUBSTRING_URI));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }
}
