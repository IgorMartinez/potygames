package br.com.igormartinez.potygames.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserToUserDTOMapper;
import br.com.igormartinez.potygames.models.Permission;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.PermissionRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserToUserDTOMapper userDTOMapper;
    private final PermissionRepository permissionRepository;
    private final PasswordManager passwordManager;
    private final SecurityContextManager securityContextManager;

    public UserService(
            UserRepository repository, 
            UserToUserDTOMapper userDTOMapper,
            PermissionRepository permissionRepository, 
            PasswordManager passwordManager,
            SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.userDTOMapper = userDTOMapper;
        this.permissionRepository = permissionRepository;
        this.passwordManager = passwordManager;
        this.securityContextManager = securityContextManager;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDTO signup(UserRegistrationDTO registrationDTO) {
        
        if (repository.existsByEmail(registrationDTO.email()))
            throw new ResourceAlreadyExistsException("The email is already in use.");

        User user = new User();
        user.setEmail(registrationDTO.email());
        user.setName(registrationDTO.name());
        user.setPassword(passwordManager.encodePassword(registrationDTO.password()));
        user.setBirthDate(registrationDTO.birthDate());
        user.setDocumentNumber(
            registrationDTO.documentNumber() != null && registrationDTO.documentNumber().isBlank() 
            ? null
            : registrationDTO.documentNumber());
        user.setPhoneNumber(
            registrationDTO.phoneNumber() != null && registrationDTO.phoneNumber().isBlank() 
            ? null
            : registrationDTO.phoneNumber());
        user.setAccountNonExpired(Boolean.TRUE);
        user.setAccountNonLocked(Boolean.TRUE);
        user.setCredentialsNonExpired(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);

        Permission permission = permissionRepository.findByDescription(PermissionType.CUSTOMER.getValue());
        user.setPermissions(List.of(permission));

        User createdUser = repository.save(user);
        return userDTOMapper.apply(createdUser);
    }

    public List<UserDTO> findAll() {
        if (!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        return repository.findAll()
            .stream()
            .map(userDTOMapper)
            .collect(Collectors.toList());
    }

    public UserDTO findById(Long id){
        if (id == null || id <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");

        if (!securityContextManager.checkSameUserOrAdmin(id))
            throw new UserUnauthorizedException();

        return repository.findById(id)
            .map(userDTOMapper)
            .orElseThrow(() -> new ResourceNotFoundException("The user was not found with the given ID."));
    }

    public UserDTO updatePersonaInformation(Long id, UserPersonalInformationDTO userDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");

        if (id != userDTO.id())
            throw new RequestValidationException("The ID in the request body must match the value of the user-id parameter.");

        if (!securityContextManager.checkSameUserOrAdmin(id))
            throw new UserUnauthorizedException();

        User user = repository.findById(userDTO.id())
            .orElseThrow(() -> new ResourceNotFoundException("The user was not found with the given ID."));
            
        user.setName(userDTO.name());
        user.setBirthDate(userDTO.birthDate());
        user.setDocumentNumber(
            userDTO.documentNumber() != null && userDTO.documentNumber().isBlank() 
            ? null
            : userDTO.documentNumber());
        user.setPhoneNumber(
            userDTO.phoneNumber() != null && userDTO.phoneNumber().isBlank() 
            ? null
            : userDTO.phoneNumber());

        User updatedUser = repository.save(user);
        return userDTOMapper.apply(updatedUser);
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");

        if (!securityContextManager.checkSameUserOrAdmin(id))
            throw new UserUnauthorizedException();

        if (!repository.existsById(id))
            throw new ResourceNotFoundException("The user was not found with the given ID.");
        
        repository.deleteById(id);
    }
}
