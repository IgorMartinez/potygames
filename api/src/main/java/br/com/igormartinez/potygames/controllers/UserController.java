package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.services.UserService;

@RestController
@RequestMapping("api/user/v1")
public class UserController {

    @Autowired
    UserService service;

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody UserRegistrationDTO registrationDTO) {
        return service.signup(registrationDTO);
    }
    
    @GetMapping
    public List<UserDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PutMapping
    public UserDTO update(@RequestBody UserDTO userDTO) {
        return service.update(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
