package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.models.UserAddress;

@Service
public class UserAddressDTOMapper implements Function<UserAddress, UserAddressDTO> {

    @Override
    public UserAddressDTO apply(UserAddress address) {
        return new UserAddressDTO(
            address.getId(), 
            address.getUser().getId(), 
            address.getFavorite(), 
            address.getBillingAddress(), 
            address.getDescription(), 
            address.getStreet(), 
            address.getNumber(), 
            address.getComplement(), 
            address.getNeighborhood(), 
            address.getCity(), 
            address.getState(), 
            address.getCountry(), 
            address.getZipCode()
        );
    }
    
}
