package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.request.UserAddressCreateDTO;
import br.com.igormartinez.potygames.data.request.UserAddressUpdateDTO;
import br.com.igormartinez.potygames.data.response.UserAddressDTO;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.models.UserAddress;

public class MockUserAddress {
    
    public UserAddress mockEntity(Integer number, User user) {
        UserAddress address = new UserAddress();
        address.setId(number.longValue());
        address.setUser(user);
        address.setFavorite((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        address.setBillingAddress((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        address.setDescription("Description " + number);
        address.setStreet("Street " + number);
        address.setNumber("Number " + number);
        address.setComplement("Complement " + number);
        address.setNeighborhood("Neighborhood " + number);
        address.setCity("City " + number);
        address.setState("State " + number);
        address.setCountry("Country " + number);
        address.setZipCode("00000-" + (String.format("%03d", number%1000)));
        return address;
    }

    public List<UserAddress> mockEntityList(Integer number, User user) {
        List<UserAddress> list = new ArrayList<>();
        for (int i=1; i<=number; i++)
            list.add(mockEntity(i, user));
        return list;
    }

    public UserAddressDTO mockDTO(Integer number, Integer idUser) {
        return new UserAddressDTO(
            number.longValue(), 
            idUser.longValue(), 
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, 
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, 
            "Description " + number, 
            "Street " + number, 
            "Number " + number, 
            "Complement " + number, 
            "Neighborhood " + number, 
            "City " + number, 
            "State " + number, 
            "Country " + number, 
            "00000-" + (String.format("%03d", number%1000)));
    }

    public List<UserAddressDTO> mockDTOList(Integer number, Integer idUser) {
        List<UserAddressDTO> list = new ArrayList<>();
        for (int i=1; i<=number; i++)
            list.add(mockDTO(i, idUser));
        return list;
    }

    public UserAddressCreateDTO mockCreateDTO(Integer number, Integer idUser) {
        return new UserAddressCreateDTO(
            idUser.longValue(), 
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, 
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, 
            "Description " + number, 
            "Street " + number, 
            "Number " + number, 
            "Complement " + number, 
            "Neighborhood " + number, 
            "City " + number, 
            "State " + number, 
            "Country " + number, 
            "00000-" + (String.format("%03d", number%1000)));
    }

    public UserAddressUpdateDTO mockUpdateDTO(Integer number, Integer idUser) {
        return new UserAddressUpdateDTO(
            number.longValue(),
            idUser.longValue(), 
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, 
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, 
            "Description " + number, 
            "Street " + number, 
            "Number " + number, 
            "Complement " + number, 
            "Neighborhood " + number, 
            "City " + number, 
            "State " + number, 
            "Country " + number, 
            "00000-" + (String.format("%03d", number%1000)));
    }
}
