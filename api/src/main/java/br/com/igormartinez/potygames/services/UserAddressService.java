package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.UserAddressCreateDTO;
import br.com.igormartinez.potygames.data.request.UserAddressUpdateDTO;
import br.com.igormartinez.potygames.data.response.UserAddressDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserAddressToUserAddressDTOMapper;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.models.UserAddress;
import br.com.igormartinez.potygames.repositories.UserAddressRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class UserAddressService {

    private final UserAddressRepository repository;
    private final UserAddressToUserAddressDTOMapper mapper;
    private final UserRepository userRepository;
    private final SecurityContextManager securityContextManager;

    public UserAddressService(
            UserAddressRepository repository, 
            UserAddressToUserAddressDTOMapper mapper,
            UserRepository userRepository,
            SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.securityContextManager = securityContextManager;
    }

    public List<UserAddressDTO> findAllByIdUser(Long idUser) {
        if (idUser == null || idUser <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        return repository.findAllByUserId(idUser)
            .stream()
            .map(mapper)
            .toList();
    }

    public UserAddressDTO findById(Long idUser, Long idAddress) {
        if (idUser == null || idUser <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");
        
        if (idAddress == null || idAddress <= 0)
            throw new RequestValidationException("The address-id must be a positive integer value.");

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        return repository.findByIdAndUserId(idAddress, idUser)
            .map(mapper)
            .orElseThrow(() -> new ResourceNotFoundException("The address was not found with the given ID."));
    }

    public UserAddressDTO create(Long idUser, UserAddressCreateDTO addressDTO) {
        if (idUser == null || idUser <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");

        if (addressDTO.idUser().compareTo(idUser) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the user-id parameter.");

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();
        
        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new ResourceNotFoundException("The user was not found with the given ID."));

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setFavorite(addressDTO.favorite());
        address.setBillingAddress(addressDTO.billingAddress());
        address.setDescription(addressDTO.description());
        address.setStreet(addressDTO.street());
        address.setNumber(addressDTO.number());
        address.setComplement(addressDTO.complement());
        address.setNeighborhood(addressDTO.neighborhood());
        address.setCity(addressDTO.city());
        address.setState(addressDTO.state());
        address.setCountry(addressDTO.country());
        address.setZipCode(addressDTO.zipCode());

        return mapper.apply(repository.save(address));
    }

    public UserAddressDTO update(Long idUser, Long idAddress, UserAddressUpdateDTO addressDTO) {
        if (idUser == null || idUser <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");
        
        if (idAddress == null || idAddress <= 0)
            throw new RequestValidationException("The address-id must be a positive integer value.");

        if (addressDTO.idUser().compareTo(idUser) != 0)
            throw new RequestValidationException("The ID of user in the request body must match the value of the user-id parameter.");

        if (addressDTO.id().compareTo(idAddress) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the address-id parameter.");

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        UserAddress address = repository.findByIdAndUserId(idAddress, idUser)
            .orElseThrow(() -> new ResourceNotFoundException("The address was not found with the given ID."));
        
        address.setFavorite(addressDTO.favorite());
        address.setBillingAddress(addressDTO.billingAddress());
        address.setDescription(addressDTO.description());
        address.setStreet(addressDTO.street());
        address.setNumber(addressDTO.number());
        address.setComplement(addressDTO.complement());
        address.setNeighborhood(addressDTO.neighborhood());
        address.setCity(addressDTO.city());
        address.setState(addressDTO.state());
        address.setCountry(addressDTO.country());
        address.setZipCode(addressDTO.zipCode());

        return mapper.apply(repository.save(address));
    }

    public void delete(Long idUser, Long idAddress) {
        if (idUser == null || idUser <= 0)
            throw new RequestValidationException("The user-id must be a positive integer value.");
        
        if (idAddress == null || idAddress <= 0)
            throw new RequestValidationException("The address-id must be a positive integer value.");
        
        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        UserAddress address = repository.findByIdAndUserId(idAddress, idUser)
            .orElseThrow(() -> new ResourceNotFoundException("The address was not found with the given ID."));
        
        repository.delete(address);
    }
}
