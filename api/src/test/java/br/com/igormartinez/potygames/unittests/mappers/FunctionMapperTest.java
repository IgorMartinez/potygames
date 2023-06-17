package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.mappers.UserAddressDTOMapper;
import br.com.igormartinez.potygames.mappers.UserDTOMapper;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.mocks.MockUserAddress;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.models.UserAddress;

public class FunctionMapperTest {
    
    MockUser mockUser;
    MockUserAddress mockUserAddress;

    UserDTOMapper userDTOMapper;
    UserAddressDTOMapper userAddressDTOMapper;

    @BeforeEach
    public void setup() {
        mockUser = new MockUser();
        mockUserAddress = new MockUserAddress();
        
        userDTOMapper = new UserDTOMapper();
        userAddressDTOMapper = new UserAddressDTOMapper();
    }

    @Test
    public void testUserDTOMapper() {
        User user = mockUser.mockUser(1);
                
        UserDTO userDTO = userDTOMapper.apply(user);
        
        assertEquals(1L, userDTO.id());
        assertEquals("user_mail1@test.com", userDTO.email());
        assertEquals("User name 1", userDTO.name());
        assertEquals(LocalDate.of(1951,2,2), userDTO.birthDate());
        assertEquals("000.000.000-01", userDTO.documentNumber());
        assertEquals("+5500900000001", userDTO.phoneNumber());
        assertFalse(userDTO.accountNonExpired());
        assertFalse(userDTO.accountNonLocked());
        assertFalse(userDTO.credentialsNonExpired());
        assertFalse(userDTO.enabled());
        assertEquals(1, userDTO.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), userDTO.permissions().get(0));
    }

    @Test
    public void testUserAddressDTOMapper() {
        UserAddress userAddress = mockUserAddress.mockEntity(1, mockUser.mockUser(1));

        UserAddressDTO userAddressDTO = userAddressDTOMapper.apply(userAddress);

        assertEquals(Long.valueOf(1L), userAddressDTO.id());
        assertEquals(Long.valueOf(1L), userAddressDTO.idUser());
        assertFalse(userAddressDTO.favorite());
        assertFalse(userAddressDTO.billingAddress());
        assertEquals("Description 1", userAddressDTO.description());
        assertEquals("Street 1", userAddressDTO.street());
        assertEquals("Number 1", userAddressDTO.number());
        assertEquals("Complement 1", userAddressDTO.complement());
        assertEquals("Neighborhood 1", userAddressDTO.neighborhood());
        assertEquals("City 1", userAddressDTO.city());
        assertEquals("State 1", userAddressDTO.state());
        assertEquals("Country 1", userAddressDTO.country());
        assertEquals("00000-001", userAddressDTO.zipCode());
    }
}
