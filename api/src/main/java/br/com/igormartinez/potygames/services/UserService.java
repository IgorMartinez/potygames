package br.com.igormartinez.potygames.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.RequiredObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserDTOMapper;
import br.com.igormartinez.potygames.models.Permission;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.PermissionRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserDTOMapper userDTOMapper;
    private final PermissionRepository permissionRepository;
    private final PasswordManager passwordManager;
    private final SecurityContextManager securityContextManager;

    public UserService(
            UserRepository repository, 
            UserDTOMapper userDTOMapper,
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
        if (registrationDTO == null
            || registrationDTO.email() == null || registrationDTO.email().isBlank()
            || registrationDTO.password() == null || registrationDTO.password().isBlank()
            || registrationDTO.name() == null || registrationDTO.name().isBlank()
            || registrationDTO.birthDate() == null 
            || registrationDTO.documentNumber() == null || registrationDTO.documentNumber().isBlank()) 
            throw new RequiredObjectIsNullException("Request object cannot be null");

        if (repository.existsByEmail(registrationDTO.email()))
            throw new ResourceAlreadyExistsException("User alrealdy exists");

        User user = new User();
        user.setEmail(registrationDTO.email());
        user.setName(registrationDTO.name());
        user.setPassword(passwordManager.encodePassword(registrationDTO.password()));
        user.setBirthDate(registrationDTO.birthDate());
        user.setDocumentNumber(registrationDTO.documentNumber());
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
        if (!securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN))
            throw new UserUnauthorizedException("The user not have permission to this resource");

        return repository.findAll()
            .stream()
            .map(userDTOMapper)
            .collect(Collectors.toList());
    }

    public UserDTO findById(Long id){
        if (id == null || id <= 0)
            throw new RequiredObjectIsNullException("ID cannot be null or less than zero");

        if (!securityContextManager.verifyIdUserAuthenticated(id)
            && !securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN))
            throw new UserUnauthorizedException("The user not have permission to this resource");

        return repository.findById(id)
            .map(userDTOMapper)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserPersonalInformationDTO updatePersonaInformation(Long id, UserPersonalInformationDTO userDTO) {
        if (userDTO == null 
            || userDTO.id() == null || userDTO.id() <= 0 || userDTO.id() != id
            || userDTO.name() == null || userDTO.name().isBlank()
            || userDTO.birthDate() == null
            || userDTO.documentNumber() == null || userDTO.documentNumber().isBlank())
                throw new RequiredObjectIsNullException("Request object cannot be null");

        if (!securityContextManager.verifyIdUserAuthenticated(userDTO.id())
            && !securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN))
            throw new UserUnauthorizedException("The user not have permission to this resource");

        User user = repository.findById(userDTO.id())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(userDTO.name());
        user.setBirthDate(userDTO.birthDate());
        user.setDocumentNumber(userDTO.documentNumber());

        User updatedUser = repository.save(user);
        return new UserPersonalInformationDTO(
                updatedUser.getId(), updatedUser.getName(), 
                updatedUser.getBirthDate(), updatedUser.getDocumentNumber());
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequiredObjectIsNullException("ID cannot be null or less than zero");

        if (!securityContextManager.verifyIdUserAuthenticated(id)
            && !securityContextManager.verifyPermissionUserAuthenticated(PermissionType.ADMIN))
            throw new UserUnauthorizedException("The user not have permission to this resource");

        if (!repository.existsById(id))
            throw new ResourceNotFoundException("User not found");
        
        repository.deleteById(id);
    }
}
