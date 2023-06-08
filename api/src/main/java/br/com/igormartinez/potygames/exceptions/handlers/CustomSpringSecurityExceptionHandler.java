package br.com.igormartinez.potygames.exceptions.handlers;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSpringSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Authentication required", request.getRequestURI());
        response.getWriter().write(exceptionResponse.toJsonString());
        response.setStatus(401);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Not authorized", request.getRequestURI());
        response.getWriter().write(exceptionResponse.toJsonString());
        response.setStatus(401);
    }

}
