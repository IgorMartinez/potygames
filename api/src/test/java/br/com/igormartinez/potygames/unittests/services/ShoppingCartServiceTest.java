package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.igormartinez.potygames.data.request.ShoppingCartItemRequestDTO;
import br.com.igormartinez.potygames.data.response.ShoppingCartItemResponseDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ShoppingCartItemEntityToDTOMapper;
import br.com.igormartinez.potygames.mocks.InventoryItemMocker;
import br.com.igormartinez.potygames.mocks.ShoppingCartMocker;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.ShoppingCartItem;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ShoppingCartItemRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.ShoppingCartService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    
    private ShoppingCartService service;
    
    @Mock
    private ShoppingCartItemRepository repository;
    
    @Mock
    private InventoryItemRepository itemRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SecurityContextManager securityContextManager;
    
    @BeforeEach
    void setup() {
        service = new ShoppingCartService(
            repository, 
            itemRepository, 
            userRepository, 
            securityContextManager, 
            new ShoppingCartItemEntityToDTOMapper());
    }

    @Test
    void testFindAllByUserWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findAllByUser(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindAllByUserWithItems() {
        List<ShoppingCartItem> entityList = ShoppingCartMocker.mockEntityList(5);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(repository.findAllByUserId(1L)).thenReturn(entityList);

        List<ShoppingCartItemResponseDTO> output = service.findAllByUser(1L);
        assertEquals(5, output.size());

        ShoppingCartItemResponseDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.idInventoryItem());
        assertEquals("Product name 1", outputPosition0.name());
        assertEquals("Version 1", outputPosition0.version());
        assertEquals("Condition 1", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("1.99")));
        assertEquals(1, outputPosition0.quantity());

        ShoppingCartItemResponseDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.idInventoryItem());
        assertEquals("Product name 5", outputPosition4.name());
        assertEquals("Version 5", outputPosition4.version());
        assertEquals("Condition 5", outputPosition4.condition());
        assertEquals(0, outputPosition4.price().compareTo(new BigDecimal("5.99")));
        assertEquals(5, outputPosition4.quantity());
    }

    @Test
    void testFindAllByUserWithoutItems() {
        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(repository.findAllByUserId(1L)).thenReturn(new ArrayList<>());

        List<ShoppingCartItemResponseDTO> output = service.findAllByUser(1L);
        assertEquals(0, output.size());
    }

    @Test
    void testAddItemToCartWithoutPermission() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(1L, 1);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.addItemToCart(1L, requestDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testAddItemToCartWithUserNotFound() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(1L, 1);
        
        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.addItemToCart(1L, requestDTO);
        });
        String expectedMessage = "The user was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testAddItemToCartWithInventoryItemNotFound() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(1L, 1);
        User user = MockUser.mockEntity(1);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(requestDTO.idInventoryItem())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.addItemToCart(1L, requestDTO);
        });
        String expectedMessage = "The inventory item was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testAddItemToCartWithItemAlreadyAdd() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(1L, 1);
        User user = MockUser.mockEntity(1);
        InventoryItem inventoryItem = InventoryItemMocker.mockEntity(1);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(requestDTO.idInventoryItem())).thenReturn(Optional.of(inventoryItem));
        when(repository.existsByUserIdAndItemId(1L, requestDTO.idInventoryItem())).thenReturn(Boolean.TRUE);

        Exception output = assertThrows(ResourceAlreadyExistsException.class, () -> {
            service.addItemToCart(1L, requestDTO);
        });
        String expectedMessage = "The inventory item was already add to the cart.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testAddItemToCartWithSuccess() {
        Long idUser = 3L;
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(2L, 5);
        User user = MockUser.mockEntity(idUser.intValue());
        InventoryItem inventoryItem = InventoryItemMocker.mockEntity(requestDTO.idInventoryItem().intValue());
        ShoppingCartItem item = ShoppingCartMocker.mockEntity(9L, user, inventoryItem, requestDTO.quantity());
        
        when(securityContextManager.checkSameUserOrAdmin(idUser)).thenReturn(Boolean.TRUE);
        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));
        when(itemRepository.findById(requestDTO.idInventoryItem())).thenReturn(Optional.of(inventoryItem));
        when(repository.existsByUserIdAndItemId(idUser, requestDTO.idInventoryItem())).thenReturn(Boolean.FALSE);
        when(repository.save(any(ShoppingCartItem.class))).thenReturn(item);

        // Check result after save
        ShoppingCartItemResponseDTO output = service.addItemToCart(idUser, requestDTO);
        assertEquals(2L, output.idInventoryItem());
        assertEquals("Product name 2", output.name());
        assertEquals("Version 2", output.version());
        assertEquals("Condition 2", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("2.99")));
        assertEquals(5, output.quantity());

        // Check result before save
        ArgumentCaptor<ShoppingCartItem> argumentCaptor = ArgumentCaptor.forClass(ShoppingCartItem.class);
        verify(repository).save(argumentCaptor.capture());
        ShoppingCartItem capturedObject = argumentCaptor.getValue();
        assertNull(capturedObject.getId());
        assertEquals(3L, capturedObject.getUser().getId());
        assertEquals("User name 3", capturedObject.getUser().getName());
        assertEquals(2L, capturedObject.getItem().getId());
        assertEquals("Version 2", capturedObject.getItem().getVersion());
        assertEquals(5, capturedObject.getQuantity());
    }

    @Test
    void testUpdateItemInCartWithMismatchParamIdAndDTOId() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(1L, 1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.updateItemInCart(1L, 2L, requestDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the inventory-item-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateItemInCartWithoutPermission() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(2L, 5);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.updateItemInCart(1L, 2L, requestDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateItemInCartWithItemNotFound() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(2L, 5);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(repository.findByUserIdAndItemId(1L, 2L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.updateItemInCart(1L, 2L, requestDTO);
        });
        String expectedMessage = "The inventory item was not added to the cart.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateItemInCartWithSuccess() {
        ShoppingCartItemRequestDTO requestDTO = new ShoppingCartItemRequestDTO(2L, 5);
        ShoppingCartItem item = ShoppingCartMocker.mockEntity(2);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(repository.findByUserIdAndItemId(1L, 2L)).thenReturn(Optional.of(item));
        when(repository.save(item)).thenReturn(item);

        // Check result after save
        ShoppingCartItemResponseDTO output = service.updateItemInCart(1L, 2L, requestDTO);
        assertEquals(2L, output.idInventoryItem());
        assertEquals("Product name 2", output.name());
        assertEquals("Version 2", output.version());
        assertEquals("Condition 2", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("2.99")));
        assertEquals(5, output.quantity());

        // Check result before save
        ArgumentCaptor<ShoppingCartItem> argumentCaptor = ArgumentCaptor.forClass(ShoppingCartItem.class);
        verify(repository).save(argumentCaptor.capture());
        ShoppingCartItem capturedObject = argumentCaptor.getValue();
        assertEquals(2L, capturedObject.getId());
        assertEquals(2L, capturedObject.getUser().getId());
        assertEquals("User name 2", capturedObject.getUser().getName());
        assertEquals(2L, capturedObject.getItem().getId());
        assertEquals("Version 2", capturedObject.getItem().getVersion());
        assertEquals(5, capturedObject.getQuantity());
    }

    @Test
    void testRemoveItemFromCartWithoutPermission() {
        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.removeItemFromCart(1L, 2L);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRemoveItemFromCartWithItemNotFound() {
        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(repository.findByUserIdAndItemId(1L, 2L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.removeItemFromCart(1L, 2L);
        });
        String expectedMessage = "The inventory item was not added to the cart.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testRemoveItemFromCartWithSuccess() {
        ShoppingCartItem item = ShoppingCartMocker.mockEntity(1);

        when(securityContextManager.checkSameUserOrAdmin(1)).thenReturn(Boolean.TRUE);
        when(repository.findByUserIdAndItemId(1L, 2L)).thenReturn(Optional.of(item));

        service.removeItemFromCart(1L, 2L);
    }
}
