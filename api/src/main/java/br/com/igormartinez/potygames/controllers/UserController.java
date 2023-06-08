package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.services.UserService;

@RestController
@RequestMapping("api/user/v1")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping
    public List<User> getUsers() {
        return service.findAll();
    }
}
