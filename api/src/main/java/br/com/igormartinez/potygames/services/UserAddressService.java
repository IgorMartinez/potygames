package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserAddressDTOMapper;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.models.UserAddress;
import br.com.igormartinez.potygames.repositories.UserAddressRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class UserAddressService {

    private final UserAddressRepository repository;
    private final UserAddressDTOMapper mapper;
    private final UserRepository userRepository;
    private final SecurityContextManager securityContextManager;

    public UserAddressService(
            UserAddressRepository repository, 
            UserAddressDTOMapper mapper,
            UserRepository userRepository,
            SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.securityContextManager = securityContextManager;
    }

    public List<UserAddressDTO> findAllByIdUser(Long idUser) {
        if (idUser == null || idUser <= 0)
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        return repository.findAllByUserId(idUser)
            .stream()
            .map(mapper)
            .toList();
    }

    public UserAddressDTO findById(Long idUser, Long idAddress) {
        if (idUser == null || idUser <= 0
            || idAddress == null || idAddress <= 0)
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        return repository.findByIdAndUserId(idAddress, idUser)
            .map(mapper)
            .orElseThrow(() -> new ResourceNotFoundException());
    }

    public UserAddressDTO create(Long idUser, UserAddressDTO addressDTO) {
        if (idUser == null || idUser <= 0
            || addressDTO == null
            || (addressDTO.idUser() != null && addressDTO.idUser() != idUser))
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();
        
        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new ResourceNotFoundException());

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

    public UserAddressDTO update(Long idUser, Long idAddress, UserAddressDTO addressDTO) {
        if (idUser == null || idUser <= 0
            || idAddress == null || idAddress <= 0
            || addressDTO == null
            || addressDTO.id() == null || addressDTO.id() != idAddress
            || addressDTO.idUser() == null || addressDTO.idUser() != idUser)
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        UserAddress address = repository.findByIdAndUserId(idAddress, idUser)
            .orElseThrow(() -> new ResourceNotFoundException());
        
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
        if (idUser == null || idUser <= 0
            || idAddress == null || idAddress <= 0)
            throw new RequestObjectIsNullException();
        
        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        UserAddress address = repository.findByIdAndUserId(idAddress, idUser)
            .orElseThrow(() -> new ResourceNotFoundException());
        
        repository.delete(address);
    }
}
