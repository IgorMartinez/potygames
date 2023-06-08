package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);
        
        if (user == null) 
            throw new UsernameNotFoundException("User with email " + email + " not found");

        return user;
    }

    public List<User> findAll() {
        return repository.findAll();
    }
}
