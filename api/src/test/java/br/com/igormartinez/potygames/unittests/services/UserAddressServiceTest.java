package br.com.igormartinez.potygames.unittests.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.UserAddressDTOMapper;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.mocks.MockUserAddress;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.models.UserAddress;
import br.com.igormartinez.potygames.repositories.UserAddressRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.UserAddressService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserAddressServiceTest {

    private MockUserAddress mockUserAddress;
    private MockUser mockUser;
    private UserAddressService service;

    @Mock
    private UserAddressRepository repository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        mockUserAddress = new MockUserAddress();
        mockUser = new MockUser();

        service = new UserAddressService(
            repository, 
            new UserAddressDTOMapper(), 
            userRepository, 
            securityContextManager);
    }

    @Test
    void testFindAllByIdUserWithParamNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findAllByIdUser(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindAllByIdUserWithParamZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findAllByIdUser(0L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindAllByIdUserWithParamNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findAllByIdUser(-550L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindAllByIdUserWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findAllByIdUser(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindAllByIdUserWithPermission() {
        List<UserAddress> listUserAddress = 
            mockUserAddress.mockEntityList(10, mockUser.mockUser(1));

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findAllByUserId(1L)).thenReturn(listUserAddress);

        List<UserAddressDTO> output = service.findAllByIdUser(1L);

        assertEquals(10, output.size());

        UserAddressDTO outputPosition0 = output.get(0);
        assertEquals(Long.valueOf(1L), outputPosition0.id());
        assertEquals(Long.valueOf(1L), outputPosition0.idUser());
        assertFalse(outputPosition0.favorite());
        assertFalse(outputPosition0.billingAddress());
        assertEquals("Description 1", outputPosition0.description());
        assertEquals("Street 1", outputPosition0.street());
        assertEquals("Number 1", outputPosition0.number());
        assertEquals("Complement 1", outputPosition0.complement());
        assertEquals("Neighborhood 1", outputPosition0.neighborhood());
        assertEquals("City 1", outputPosition0.city());
        assertEquals("State 1", outputPosition0.state());
        assertEquals("Country 1", outputPosition0.country());
        assertEquals("00000-001", outputPosition0.zipCode());

        UserAddressDTO outputPosition3 = output.get(3);
        assertEquals(Long.valueOf(4L), outputPosition3.id());
        assertEquals(Long.valueOf(1L), outputPosition3.idUser());
        assertTrue(outputPosition3.favorite());
        assertTrue(outputPosition3.billingAddress());
        assertEquals("Description 4", outputPosition3.description());
        assertEquals("Street 4", outputPosition3.street());
        assertEquals("Number 4", outputPosition3.number());
        assertEquals("Complement 4", outputPosition3.complement());
        assertEquals("Neighborhood 4", outputPosition3.neighborhood());
        assertEquals("City 4", outputPosition3.city());
        assertEquals("State 4", outputPosition3.state());
        assertEquals("Country 4", outputPosition3.country());
        assertEquals("00000-004", outputPosition3.zipCode());

        UserAddressDTO outputPosition9 = output.get(9);
        assertEquals(Long.valueOf(10L), outputPosition9.id());
        assertEquals(Long.valueOf(1L), outputPosition9.idUser());
        assertTrue(outputPosition9.favorite());
        assertTrue(outputPosition9.billingAddress());
        assertEquals("Description 10", outputPosition9.description());
        assertEquals("Street 10", outputPosition9.street());
        assertEquals("Number 10", outputPosition9.number());
        assertEquals("Complement 10", outputPosition9.complement());
        assertEquals("Neighborhood 10", outputPosition9.neighborhood());
        assertEquals("City 10", outputPosition9.city());
        assertEquals("State 10", outputPosition9.state());
        assertEquals("Country 10", outputPosition9.country());
        assertEquals("00000-010", outputPosition9.zipCode());
    }

    @Test
    void testFindAllByIdUserWithReturnEmpty() {
        List<UserAddress> listUserAddress = new ArrayList<>();

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findAllByUserId(1L)).thenReturn(listUserAddress);

        List<UserAddressDTO> output = service.findAllByIdUser(1L);
        assertEquals(0, output.size());
    }
    
    @Test
    void testFindByIdWithParamIdUserNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(null, 1L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdUserZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(0L, 1L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdUserNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(-90L, 1L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdAddressNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdAddressZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(1L, 0L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdAddressNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(1L, -9L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findById(1L, 1L);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithPermission() {
        UserAddress address = mockUserAddress.mockEntity(1, mockUser.mockUser(1));

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));

        UserAddressDTO output = service.findById(1L, 1L);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals(Long.valueOf(1L), output.idUser());
        assertFalse(output.favorite());
        assertFalse(output.billingAddress());
        assertEquals("Description 1", output.description());
        assertEquals("Street 1", output.street());
        assertEquals("Number 1", output.number());
        assertEquals("Complement 1", output.complement());
        assertEquals("Neighborhood 1", output.neighborhood());
        assertEquals("City 1", output.city());
        assertEquals("State 1", output.state());
        assertEquals("Country 1", output.country());
        assertEquals("00000-001", output.zipCode());
    }

    @Test
    void testFindByIdWithAddressNotFound() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L, 1L);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamIdUserNull() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(null, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateIdWithParamIdUserZero() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(0L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamIdUserNegative() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(-55L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamDTONull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamIdUserDifferentIdUserDTO() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(2L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(1L, addressDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        User user = mockUser.mockUser(1);
        UserAddress address = mockUserAddress.mockEntity(1, user);
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(UserAddress.class))).thenReturn(address);

        UserAddressDTO output = service.create(1L, addressDTO);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals(Long.valueOf(1L), output.idUser());
        assertFalse(output.favorite());
        assertFalse(output.billingAddress());
        assertEquals("Description 1", output.description());
        assertEquals("Street 1", output.street());
        assertEquals("Number 1", output.number());
        assertEquals("Complement 1", output.complement());
        assertEquals("Neighborhood 1", output.neighborhood());
        assertEquals("City 1", output.city());
        assertEquals("State 1", output.state());
        assertEquals("Country 1", output.country());
        assertEquals("00000-001", output.zipCode());
    }

    @Test
    void testCreateWithUserNotFound() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.create(1L, addressDTO);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdUserNull() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(null, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateIdWithParamIdUserZero() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(0L, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdUserNegative() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(-55L, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdAddressNull() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, null, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateIdWithParamIdAddressZero() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, 0L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdAddressNegative() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, -12L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTONull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, 1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdNull() {
        UserAddressDTO addressDTO = new UserAddressDTO(null, null, null, 
            null, null, null, null, null, 
            null, null, null, null, null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdAndParamIdAddressDifferent() {
        UserAddressDTO addressDTO = new UserAddressDTO(1231L, null, null, 
            null, null, null, null, null, 
            null, null, null, null, null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdUserNull() {
        UserAddressDTO addressDTO = new UserAddressDTO(1L, null, null, 
            null, null, null, null, null, 
            null, null, null, null, null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdUserAndParamIdUserDifferent() {
        UserAddressDTO addressDTO = new UserAddressDTO(1L, 1231L, null, 
            null, null, null, null, null, 
            null, null, null, null, null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, 1L, addressDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, 1L, addressDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        UserAddress address = mockUserAddress.mockEntity(1, mockUser.mockUser(1));
        UserAddressDTO addressDTO = mockUserAddress.mockUpdatedDTO(1, 1);

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(repository.save(address)).thenReturn(address);

        UserAddressDTO output = service.update(1L, 1L, addressDTO);
        assertEquals(Long.valueOf(1L), output.id());
        assertEquals(Long.valueOf(1L), output.idUser());
        assertFalse(output.favorite());
        assertFalse(output.billingAddress());
        assertEquals("Description updated 1", output.description());
        assertEquals("Street updated 1", output.street());
        assertEquals("Number updated 1", output.number());
        assertEquals("Complement updated 1", output.complement());
        assertEquals("Neighborhood updated 1", output.neighborhood());
        assertEquals("City updated 1", output.city());
        assertEquals("State updated 1", output.state());
        assertEquals("Country updated 1", output.country());
        assertEquals("00001-001", output.zipCode());
    }

    @Test
    void testUpdateWitAddressNotFound() {
        UserAddressDTO addressDTO = mockUserAddress.mockDTO(1, 1);

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, 1L, addressDTO);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdUserNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(null, 1L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdUserZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(0L, 1L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdUserNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(-99L, 1L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdAddressNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdAddressZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdAddressNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.delete(1L, 1L);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithPermission() {
        UserAddress address = mockUserAddress.mockEntity(1, mockUser.mockUser(1));

        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));

        service.delete(1L, 1L);
    }

    @Test
    void testDeleteWitAddressNotFound() {
        when(securityContextManager.checkSameUserOrAdmin(1L)).thenReturn(Boolean.TRUE);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L, 1L);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

}
