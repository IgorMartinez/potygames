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
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.services.UserService;

@RestController
@RequestMapping("api/user/v1")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public UserRegistrationDTO create(@RequestBody UserRegistrationDTO userDTO) {
        return service.create(userDTO);
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
