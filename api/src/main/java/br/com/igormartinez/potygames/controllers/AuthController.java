package br.com.igormartinez.potygames.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    AuthService service;

    @Operation(
        summary = "Authenticates a user and return a token",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PostMapping("/signin")
    public Token signin(@RequestBody AccountCredentials accountCredentials) {
        return service.signin(accountCredentials);
    }

    @Operation(
        summary = "Refresh token for authenticated user and returns a token",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PutMapping("/refresh")
    @SuppressWarnings("rawtypes")
    public ResponseEntity refresh(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(service.refresh(refreshToken));
    }
}
