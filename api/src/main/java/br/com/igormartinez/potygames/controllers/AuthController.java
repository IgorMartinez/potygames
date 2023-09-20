package br.com.igormartinez.potygames.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    AuthService service;

    @Operation(
        summary = "Signup a user",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PostMapping("/signup")
    public UserDTO signup(@RequestBody @Valid UserRegistrationDTO registrationDTO) {
        return service.signup(registrationDTO);
    }

    @Operation(
        summary = "Authenticates a user and return a token",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PostMapping("/signin")
    public Token signin(@RequestBody @Valid AccountCredentials accountCredentials) {
        return service.signin(accountCredentials);
    }

    @Operation(
        summary = "Refresh token for authenticated user and returns a token",
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @PutMapping("/refresh")
    public Token refresh(@RequestHeader("Authorization") String refreshToken) {
        return service.refresh(refreshToken);
    }
}
