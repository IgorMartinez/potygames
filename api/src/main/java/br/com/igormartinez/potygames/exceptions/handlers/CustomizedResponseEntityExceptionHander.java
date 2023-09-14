package br.com.igormartinez.potygames.exceptions.handlers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.exceptions.InvalidTokenException;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceInsufficientException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.TokenCreationErrorException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import br.com.igormartinez.potygames.exceptions.InvalidUsernamePasswordException;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHander extends ResponseEntityExceptionHandler {

    // Example:
    // request.getDescription(false) = uri=/auth/signin
    // request.getDescription(false).substring(SUBSTRING_URI) = /auth/signin
    private final int SUBSTRING_URI = 4;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, 
        HttpStatusCode status, WebRequest request) {

        ProblemDetail problemDetail = ex.getBody();

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", errors);

        return super.createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request);
    }

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
        RequestValidationException.class,
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

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<APIErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        
        APIErrorResponse exceptionResponse = 
            new APIErrorResponse(
                "about:blank",
                "Bad Request", 
                HttpStatus.BAD_REQUEST.value(), 
                ex.getConstraintViolations().iterator().next().getMessage(), 
                request.getDescription(false).substring(SUBSTRING_URI),
                null);

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

    @ExceptionHandler({
        ResourceAlreadyExistsException.class,
        ResourceInsufficientException.class,
        DeleteAssociationConflictException.class})
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
