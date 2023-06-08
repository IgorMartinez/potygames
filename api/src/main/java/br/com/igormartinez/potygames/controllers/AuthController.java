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
import br.com.igormartinez.potygames.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    AuthService service;

    @PostMapping("/signin")
    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(@RequestBody AccountCredentials accountCredentials) {
        return service.signin(accountCredentials);
    }

    @PutMapping("/refresh")
    @SuppressWarnings("rawtypes")
    public ResponseEntity refresh(@RequestHeader("Authorization") String refreshToken) {
        return service.refresh(refreshToken);
    }
}
