package br.com.igormartinez.potygames.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserToUserDTOMapper;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserToUserDTOMapper userDTOMapper;
    private final SecurityContextManager securityContextManager;

    public UserService(
            UserRepository repository, 
            UserToUserDTOMapper userDTOMapper,
            SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.userDTOMapper = userDTOMapper;
        this.securityContextManager = securityContextManager;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
