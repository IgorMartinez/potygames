package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequiredObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserNotAuthorizedException;
import br.com.igormartinez.potygames.mapper.ObjectMapper;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordManager passwordManager;

    @Autowired
    SecurityContextManager securityContextManager;

    public UserService(UserRepository repository, PasswordManager passwordManager, SecurityContextManager securityContextManager) {
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);
        
        if (user == null) 
            throw new UsernameNotFoundException("User not found");

        return user;
    }

    public UserRegistrationDTO signup(UserRegistrationDTO userDTO) {
        if (userDTO == null) 
            throw new RequiredObjectIsNullException("Request object cannot be null");

        if (repository.findByEmail(userDTO.getEmail()) != null)
            throw new ResourceAlreadyExistsException("User alrealdy exists");

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPassword(passwordManager.encodePassword(userDTO.getPassword()));
        user.setAccountNonExpired(Boolean.TRUE);
        user.setAccountNonLocked(Boolean.TRUE);
        user.setCredentialsNonExpired(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);

        User createdUser = repository.save(user);

        return new UserRegistrationDTO(createdUser.getName(), createdUser.getEmail(), "");
    }

    public List<UserDTO> findAll() {
        if (!securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN))
            throw new UserNotAuthorizedException("The user not have permission to this resource");

        return ObjectMapper.parseListObjects(repository.findAll(), UserDTO.class);
    }

    public UserDTO findById(Long id){
        if (id == null || id <= 0)
            throw new RequiredObjectIsNullException("ID cannot be null or less than zero");

        if (!securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)
            || !securityContextManager.verifyIdUserAuthenticated(id))
            throw new UserNotAuthorizedException("The user not have permission to this resource");

        User user = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ObjectMapper.parseObject(user, UserDTO.class);
    }

    public UserDTO update(UserDTO userDTO) {
        if (userDTO == null 
                || userDTO.getId() == null || userDTO.getId() <= 0
                || userDTO.getName() == null || userDTO.getName().isBlank() 
                || userDTO.getEmail() == null || userDTO.getEmail().isBlank()
                || userDTO.getPassword() == null || userDTO.getPassword().isBlank()
                || userDTO.getAccountNonExpired() == null || userDTO.getAccountNonLocked() == null
                || userDTO.getCredentialsNonExpired() == null || userDTO.getEnabled() == null) 
            throw new RequiredObjectIsNullException("Request object cannot be null");

        if (!securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)
            || !securityContextManager.verifyIdUserAuthenticated(userDTO.getId()))
            throw new UserNotAuthorizedException("The user not have permission to this resource");

        if (!repository.existsById(userDTO.getId())) 
            throw new ResourceNotFoundException("User not found");
        
        User user = repository.findByEmail(userDTO.getEmail());
        if (user != null && user.getId() != userDTO.getId())
            throw new ResourceAlreadyExistsException("User alrealdy exists");

        if (user == null)
            throw new ResourceNotFoundException("User not found");

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordManager.encodePassword(userDTO.getPassword()));
        user.setAccountNonExpired(userDTO.getAccountNonExpired());
        user.setAccountNonLocked(userDTO.getAccountNonLocked());
        user.setCredentialsNonExpired(userDTO.getCredentialsNonExpired());
        user.setEnabled(userDTO.getCredentialsNonExpired());

        User updatedUser = repository.save(user);

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(updatedUser.getId());
        updatedUserDTO.setName(updatedUser.getName());
        updatedUserDTO.setEmail(updatedUser.getEmail());
        updatedUserDTO.setPassword("");
        updatedUserDTO.setAccountNonExpired(updatedUser.getAccountNonExpired());
        updatedUserDTO.setAccountNonLocked(updatedUser.getAccountNonLocked());
        updatedUserDTO.setCredentialsNonExpired(updatedUser.getCredentialsNonExpired());
        updatedUserDTO.setEnabled(updatedUser.getEnabled());

        return updatedUserDTO;
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequiredObjectIsNullException("ID cannot be null or less than zero");

        if (!securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN)
            || !securityContextManager.verifyIdUserAuthenticated(id))
            throw new UserNotAuthorizedException("The user not have permission to this resource");

        if (!repository.existsById(id))
            throw new ResourceNotFoundException("User not found");
        
        repository.deleteById(id);
    }
}
