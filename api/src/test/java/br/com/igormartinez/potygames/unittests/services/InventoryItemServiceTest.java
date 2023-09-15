package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import br.com.igormartinez.potygames.data.request.InventoryItemCreateDTO;
import br.com.igormartinez.potygames.data.request.InventoryItemUpdateDTO;
import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.InventoryItemToInventoryItemDTOMapper;
import br.com.igormartinez.potygames.mocks.InventoryItemMocker;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.InventoryItemService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class InventoryItemServiceTest {

    private InventoryItemService service;

    @Mock
    private InventoryItemRepository repository;
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        service = new InventoryItemService(
            repository,
            productRepository,
            new InventoryItemToInventoryItemDTOMapper(),
            securityContextManager
        );
    }

    @Test
    void testFindAllWithProductsPage0() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<InventoryItem> page = InventoryItemMocker.mockPage(94, pageable);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<InventoryItemDTO> outputPage = service.findAll(pageable);
        assertEquals(0, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<InventoryItemDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(10, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("Version 1", outputPosition0.version());
        assertEquals("Condition 1", outputPosition0.condition());
        assertEquals(new BigDecimal("1.99"), outputPosition0.price());
        assertEquals(1, outputPosition0.quantity());

        InventoryItemDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals(5L, outputPosition4.product());
        assertEquals("Version 5", outputPosition4.version());
        assertEquals("Condition 5", outputPosition4.condition());
        assertEquals(new BigDecimal("5.99"), outputPosition4.price());
        assertEquals(5, outputPosition4.quantity());

        InventoryItemDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals(10L, outputPosition9.product());
        assertEquals("Version 10", outputPosition9.version());
        assertEquals("Condition 10", outputPosition9.condition());
        assertEquals(new BigDecimal("10.99"), outputPosition9.price());
        assertEquals(10, outputPosition9.quantity());
    }

    @Test
    void testFindAllWithProductsPage9() {
        Pageable pageable = PageRequest.of(9, 10, Sort.by(Direction.ASC, "name"));
        Page<InventoryItem> page = InventoryItemMocker.mockPage(94, pageable);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<InventoryItemDTO> outputPage = service.findAll(pageable);
        assertEquals(9, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<InventoryItemDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(4, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(91L, outputPosition0.id());
        assertEquals(91L, outputPosition0.product());
        assertEquals("Version 91", outputPosition0.version());
        assertEquals("Condition 91", outputPosition0.condition());
        assertEquals(new BigDecimal("91.99"), outputPosition0.price());
        assertEquals(91, outputPosition0.quantity());

        InventoryItemDTO outputPosition3 = output.get(3);
        assertEquals(94L, outputPosition3.id());
        assertEquals(94L, outputPosition3.product());
        assertEquals("Version 94", outputPosition3.version());
        assertEquals("Condition 94", outputPosition3.condition());
        assertEquals(new BigDecimal("94.99"), outputPosition3.price());
        assertEquals(94, outputPosition3.quantity());
    }

    @Test
    void testFindByIdWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(-10L);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithItemFound() {
        InventoryItem item = InventoryItemMocker.mockEntity(1);

        when(repository.findById(1L)).thenReturn(Optional.of(item));

        InventoryItemDTO output = service.findById(1L);
        assertEquals(1L, output.id());
        assertEquals(1L, output.product());
        assertEquals("Version 1", output.version());
        assertEquals("Condition 1", output.condition());
        assertEquals(new BigDecimal("1.99"), output.price());
        assertEquals(1, output.quantity());
    }

    @Test
    void testFindByIdWithItemNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The inventory item was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        InventoryItemCreateDTO itemDTO = InventoryItemMocker.mockCreateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(itemDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        InventoryItemCreateDTO itemDTO = InventoryItemMocker.mockCreateDTO(1);
        InventoryItem item = InventoryItemMocker.mockEntity(1, itemDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(itemDTO.product())).thenReturn(Optional.of(item.getProduct()));
        when(repository.save(any(InventoryItem.class))).thenReturn(item);

        // Check code after save
        InventoryItemDTO output = service.create(itemDTO);
        assertEquals(1L, output.id());
        assertEquals(1L, output.product());
        assertEquals("Version 1", output.version());
        assertEquals("Condition 1", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("1.99")));
        assertEquals(1, output.quantity());

        // Check code before save
        ArgumentCaptor<InventoryItem> argumentCaptor = ArgumentCaptor.forClass(InventoryItem.class);
        verify(repository).save(argumentCaptor.capture());
        InventoryItem capturedObject = argumentCaptor.getValue();
        assertNull(capturedObject.getId());
        assertEquals(1L, capturedObject.getProduct().getId());
        assertEquals("Version 1", capturedObject.getVersion());
        assertEquals("Condition 1", capturedObject.getCondition());
        assertEquals(0, capturedObject.getPrice().compareTo(new BigDecimal("1.99")));
        assertEquals(1, capturedObject.getQuantity());
    }

    @Test
    void testCreateWithProductNotFound() {
        InventoryItemCreateDTO itemDTO = InventoryItemMocker.mockCreateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(itemDTO.product())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.create(itemDTO);
        });
        String expectedMessage = "The product was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNull() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(null, itemDTO);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(0L, itemDTO);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(-10L, itemDTO);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithMismatchParamIdAndDTOId() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(2L, itemDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the inventory-item-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, itemDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "Version updated", "Condition updated", 
            new BigDecimal("55.1"), 5);
        InventoryItem item = InventoryItemMocker.mockEntity(1);
        InventoryItem itemUpdated = InventoryItemMocker.mockEntity(itemDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(itemDTO.id())).thenReturn(Optional.of(item));
        when(productRepository.findById(itemDTO.product())).thenReturn(Optional.of(itemUpdated.getProduct()));
        when(repository.save(item)).thenReturn(itemUpdated);

        // Check code after save
        InventoryItemDTO output = service.update(1L, itemDTO);
        assertEquals(1L, output.id());
        assertEquals(1L, output.product());
        assertEquals("Version updated", output.version());
        assertEquals("Condition updated", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("55.10")));
        assertEquals(5, output.quantity());

        // Check code before save
        ArgumentCaptor<InventoryItem> argumentCaptor = ArgumentCaptor.forClass(InventoryItem.class);
        verify(repository).save(argumentCaptor.capture());
        InventoryItem capturedObject = argumentCaptor.getValue();
        assertEquals(1L, capturedObject.getId());
        assertEquals(1L, capturedObject.getProduct().getId());
        assertEquals("Version updated", capturedObject.getVersion());
        assertEquals("Condition updated", capturedObject.getCondition());
        assertEquals(0, capturedObject.getPrice().compareTo(new BigDecimal("55.10")));
        assertEquals(5, capturedObject.getQuantity());
    }

    @Test
    void testUpdateWithItemNotFound() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(itemDTO.id())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, itemDTO);
        });
        String expectedMessage = "The inventory item was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithProductNotFound() {
        InventoryItemUpdateDTO itemDTO = InventoryItemMocker.mockUpdateDTO(1);
        InventoryItem item = InventoryItemMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(itemDTO.id())).thenReturn(Optional.of(item));
        when(productRepository.findById(itemDTO.product())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, itemDTO);
        });
        String expectedMessage = "The product was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(-10L);
        });
        String expectedMessage = "The inventory-item-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithoutPermission() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithItemNotFound() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The inventory item was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithItemFound() {
        InventoryItem item = InventoryItemMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        service.delete(1L);
    }
}
