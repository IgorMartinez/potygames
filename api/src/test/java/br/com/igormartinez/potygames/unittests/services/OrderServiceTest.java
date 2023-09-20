package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import br.com.igormartinez.potygames.data.request.OrderItemResquestDTO;
import br.com.igormartinez.potygames.data.request.OrderRequestDTO;
import br.com.igormartinez.potygames.data.response.OrderDetailResponseDTO;
import br.com.igormartinez.potygames.data.response.OrderResponseDTO;
import br.com.igormartinez.potygames.enums.OrderStatus;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceInsufficientException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.OrderAddressRequestDTOToEntityMapper;
import br.com.igormartinez.potygames.mappers.OrderEntityToDetailDTOMapper;
import br.com.igormartinez.potygames.mocks.InventoryItemMocker;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.mocks.OrderMocker;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.Order;
import br.com.igormartinez.potygames.models.OrderAddress;
import br.com.igormartinez.potygames.models.OrderItem;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.OrderRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.OrderService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private OrderService service;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        service = new OrderService(
            orderRepository, 
            inventoryItemRepository, 
            new OrderAddressRequestDTOToEntityMapper(), 
            new OrderEntityToDetailDTOMapper(), 
            securityContextManager);
    }

    @Test
    void testFindAllByUserWithOrders() {
        User mockedUser = MockUser.mockEntity(1);
        List<Order> mockedOrders = OrderMocker.mockEntityList(1, 4, mockedUser);

        when(securityContextManager.getUser()).thenReturn(mockedUser);
        when(orderRepository.findAllByUserId(mockedUser.getId())).thenReturn(mockedOrders);
    
        List<OrderDetailResponseDTO> output = service.findAllByUser();
        assertEquals(4, output.size());

        OrderDetailResponseDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("CANCELED", outputPosition0.status());
        assertEquals(0, outputPosition0.totalPrice().compareTo(new BigDecimal("4.98")));
        assertEquals(2, outputPosition0.items().size());
        assertNotNull(outputPosition0.billingAddress());
        assertEquals("Street 2", outputPosition0.billingAddress().street());
        assertNotNull(outputPosition0.deliveryAddress());
        assertEquals("Street 1", outputPosition0.deliveryAddress().street());

        OrderDetailResponseDTO outputPosition3 = output.get(3);
        assertEquals(4L, outputPosition3.id());
        assertEquals("CONFIRMED", outputPosition3.status());
        assertEquals(0, outputPosition3.totalPrice().compareTo(new BigDecimal("10.98")));
        assertEquals(2, outputPosition3.items().size());
        assertNotNull(outputPosition3.billingAddress());
        assertEquals("Street 4", outputPosition3.billingAddress().street());
        assertNotNull(outputPosition3.deliveryAddress());
        assertEquals("Street 5", outputPosition3.deliveryAddress().street());
    }

    @Test
    void testFindAllByUserWithoutOrders() {
        User mockedUser = MockUser.mockEntity(1);
        List<Order> mockedOrders = new ArrayList<>();

        when(securityContextManager.getUser()).thenReturn(mockedUser);
        when(orderRepository.findAllByUserId(mockedUser.getId())).thenReturn(mockedOrders);

        List<OrderDetailResponseDTO> output = service.findAllByUser();
        assertEquals(0, output.size());
    }

    @Test
    void testFindByIdWithOrderNotFound() {
        Long id = 1L;

        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(id);
        });
        String expectedMessage = "The order was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithOtherUser() {
        Long id = 1L;
        User mockedUserOwner = MockUser.mockEntity(1);
        Order mockedOrder = OrderMocker.mockEntity(id.intValue(), mockedUserOwner, null, null);
    
        when(orderRepository.findById(id)).thenReturn(Optional.of(mockedOrder));
        when(securityContextManager.checkSameUser(mockedUserOwner.getId())).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.findById(id);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithSameUser() {
        Long id = 1L;
        User mockedUserOwner = MockUser.mockEntity(1);
        Order mockedOrder = OrderMocker.mockEntity(id.intValue(), mockedUserOwner, null, null);
    
        when(orderRepository.findById(id)).thenReturn(Optional.of(mockedOrder));
        when(securityContextManager.checkSameUser(mockedUserOwner.getId())).thenReturn(Boolean.TRUE);

        OrderDetailResponseDTO output = service.findById(id);
        assertEquals(1L, output.id());
        assertEquals("CANCELED", output.status());
        assertEquals(0, output.totalPrice().compareTo(new BigDecimal("4.98")));
        assertEquals(2, output.items().size());
        assertNotNull(output.billingAddress());
        assertEquals("Street 2", output.billingAddress().street());
        assertNotNull(output.deliveryAddress());
        assertEquals("Street 1", output.deliveryAddress().street());
    }

    @Test
    void testCreateOrderWithItemNotFoundPosition0() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(9999L, 1)
        );
        OrderRequestDTO orderRequest = OrderMocker.mockRequestDTO(
            items, null, null);

        when(inventoryItemRepository.findById(9999L)).thenReturn(Optional.ofNullable(null));
    
        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.createOrder(orderRequest);
        });
        String expectedMessage = "The inventory item was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateOrderWithItemNotFoundPosition2() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(2L, 1),
            new OrderItemResquestDTO(3L, 2),
            new OrderItemResquestDTO(4L, 3)
        );
        OrderRequestDTO orderRequest = OrderMocker.mockRequestDTO(
            items, null, null);

        InventoryItem mockedInventoryItemId2 = InventoryItemMocker.mockEntity(2);
        InventoryItem mockedInventoryItemId3 = InventoryItemMocker.mockEntity(3);

        when(inventoryItemRepository.findById(2L)).thenReturn(Optional.of(mockedInventoryItemId2));
        when(inventoryItemRepository.findById(3L)).thenReturn(Optional.of(mockedInventoryItemId3));
        when(inventoryItemRepository.findById(4L)).thenReturn(Optional.ofNullable(null));
    
        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.createOrder(orderRequest);
        });
        String expectedMessage = "The inventory item was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateOrderWithInsufficientQuantityPosition0() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(1L, 2)
        );
        OrderRequestDTO orderRequest = OrderMocker.mockRequestDTO(
            items, null, null);

        InventoryItem mockedInventoryItem = InventoryItemMocker.mockEntity(1);

        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(mockedInventoryItem));
    
        Exception output = assertThrows(ResourceInsufficientException.class, () -> {
            service.createOrder(orderRequest);
        });
        String expectedMessage = "The order exceeded the quantity in inventory.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateOrderWithInsufficientQuantityPosition2() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(2L, 1),
            new OrderItemResquestDTO(3L, 2),
            new OrderItemResquestDTO(4L, 99)
        );
        OrderRequestDTO orderRequest = OrderMocker.mockRequestDTO(
            items, null, null);

        InventoryItem mockedInventoryItemId2 = InventoryItemMocker.mockEntity(2);
        InventoryItem mockedInventoryItemId3 = InventoryItemMocker.mockEntity(3);
        InventoryItem mockedInventoryItemId4 = InventoryItemMocker.mockEntity(4);

        when(inventoryItemRepository.findById(2L)).thenReturn(Optional.of(mockedInventoryItemId2));
        when(inventoryItemRepository.findById(3L)).thenReturn(Optional.of(mockedInventoryItemId3));
        when(inventoryItemRepository.findById(4L)).thenReturn(Optional.of(mockedInventoryItemId4));
    
        Exception output = assertThrows(ResourceInsufficientException.class, () -> {
            service.createOrder(orderRequest);
        });
        String expectedMessage = "The order exceeded the quantity in inventory.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateOrderWithSuccess() {
        // Setup
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(2L, 1),
            new OrderItemResquestDTO(4L, 2)
        );
        OrderAddressRequestDTO billingAddress = OrderMocker.mockAddressRequestDTO(1);
        OrderAddressRequestDTO deliveryAddress = OrderMocker.mockAddressRequestDTO(2);
        OrderRequestDTO orderRequest = OrderMocker.mockRequestDTO(
            items, billingAddress, deliveryAddress
        );

        InventoryItem mockedInventoryItemId2 = InventoryItemMocker.mockEntity(2);
        InventoryItem mockedInventoryItemId4 = InventoryItemMocker.mockEntity(4);
        User mockedUser = MockUser.mockEntity(1);
        Order mockedPersistedOrder = new Order();
        mockedPersistedOrder.setId(1L);
        mockedPersistedOrder.setStatus(OrderStatus.CONFIRMED);

        // Mocking results
        when(inventoryItemRepository.findById(2L)).thenReturn(Optional.of(mockedInventoryItemId2));
        when(inventoryItemRepository.findById(4L)).thenReturn(Optional.of(mockedInventoryItemId4));
        when(securityContextManager.getUser()).thenReturn(mockedUser);
        when(orderRepository.save(any(Order.class))).thenReturn(mockedPersistedOrder);

        // Check code after save
        OrderResponseDTO output = service.createOrder(orderRequest);
        assertEquals(1L, output.id());
        assertEquals("CONFIRMED", output.status());

        // Check before save

        // Checks if the item quantity has been subtracted 
        ArgumentCaptor<InventoryItem> inventoryItemArgumentCaptor 
            = ArgumentCaptor.forClass(InventoryItem.class);
        verify(inventoryItemRepository, times(2))
            .save(inventoryItemArgumentCaptor.capture());
        List<InventoryItem> capturedInventoryItems = inventoryItemArgumentCaptor.getAllValues();
        assertEquals(2, capturedInventoryItems.size());
        assertEquals(2L, capturedInventoryItems.get(0).getId());
        assertEquals(1, capturedInventoryItems.get(0).getQuantity());
        assertEquals(4L, capturedInventoryItems.get(1).getId());
        assertEquals(2, capturedInventoryItems.get(1).getQuantity());

        // Check the order before save
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();
        assertNull(capturedOrder.getId());
        assertEquals(1L, capturedOrder.getUser().getId());
        assertEquals(0, capturedOrder.getTotalPrice().compareTo(new BigDecimal("12.97")));
        assertEquals(OrderStatus.CONFIRMED, capturedOrder.getStatus());

        List<OrderItem> capturedOrderItems = capturedOrder.getOrderItems();
        assertEquals(2, capturedOrderItems.size());
        assertNull(capturedOrderItems.get(0).getId());
        assertEquals(capturedOrder, capturedOrderItems.get(0).getOrder());
        assertEquals(2L, capturedOrderItems.get(0).getItem().getId());
        assertEquals(1, capturedOrderItems.get(0).getQuantity());
        assertEquals(0, capturedOrderItems.get(0).getUnitPrice().compareTo(new BigDecimal("2.99")));
        assertNull(capturedOrderItems.get(1).getId());
        assertEquals(capturedOrder, capturedOrderItems.get(1).getOrder());
        assertEquals(4L, capturedOrderItems.get(1).getItem().getId());
        assertEquals(2, capturedOrderItems.get(1).getQuantity());
        assertEquals(0, capturedOrderItems.get(1).getUnitPrice().compareTo(new BigDecimal("4.99")));
        
        List<OrderAddress> capturedOrderAddresses = capturedOrder.getOrderAddresses();
        assertEquals(2, capturedOrderAddresses.size());
        assertNull(capturedOrderAddresses.get(0).getId());
        assertEquals(capturedOrder, capturedOrderAddresses.get(0).getOrder());
        assertTrue(capturedOrderAddresses.get(0).getBillingAddress());
        assertFalse(capturedOrderAddresses.get(0).getDeliveryAddress());
        assertEquals("Street 1", capturedOrderAddresses.get(0).getStreet());
        assertEquals("Number 1", capturedOrderAddresses.get(0).getNumber());
        assertEquals("Complement 1", capturedOrderAddresses.get(0).getComplement());
        assertEquals("Neighborhood 1", capturedOrderAddresses.get(0).getNeighborhood());
        assertEquals("City 1", capturedOrderAddresses.get(0).getCity());
        assertEquals("State 1", capturedOrderAddresses.get(0).getState());
        assertEquals("Country 1", capturedOrderAddresses.get(0).getCountry());
        assertEquals("00000-001", capturedOrderAddresses.get(0).getZipCode());
        assertNull(capturedOrderAddresses.get(1).getId());
        assertEquals(capturedOrder, capturedOrderAddresses.get(1).getOrder());
        assertFalse(capturedOrderAddresses.get(1).getBillingAddress());
        assertTrue(capturedOrderAddresses.get(1).getDeliveryAddress());
        assertEquals("Street 2", capturedOrderAddresses.get(1).getStreet());
        assertEquals("Number 2", capturedOrderAddresses.get(1).getNumber());
        assertEquals("Complement 2", capturedOrderAddresses.get(1).getComplement());
        assertEquals("Neighborhood 2", capturedOrderAddresses.get(1).getNeighborhood());
        assertEquals("City 2", capturedOrderAddresses.get(1).getCity());
        assertEquals("State 2", capturedOrderAddresses.get(1).getState());
        assertEquals("Country 2", capturedOrderAddresses.get(1).getCountry());
        assertEquals("00000-002", capturedOrderAddresses.get(1).getZipCode());
    }

    @Test
    void testCancelOrderWithOrderNotFound() {
        Long id = 1L;
        
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.cancelOrder(id);
        });
        String expectedMessage = "The order was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCancelOrderWithOtherUser() {
        Long id = 1L;
        User mockedUserOwner = MockUser.mockEntity(1);
        Order mockedOrder = OrderMocker.mockEntity(1, mockedUserOwner, null, null);
        
        when(orderRepository.findById(id)).thenReturn(Optional.of(mockedOrder));
        when(securityContextManager.checkSameUser(mockedOrder.getUser().getId())).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.cancelOrder(id);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCancelOrderWithOrderAlreadyCancel() {
        Long id = 1L;
        User mockedUserOwner = MockUser.mockEntity(1);
        Order mockedOrder = OrderMocker.mockEntity(1, mockedUserOwner, null, null);
        mockedOrder.setStatus(OrderStatus.CANCELED);
        
        when(orderRepository.findById(id)).thenReturn(Optional.of(mockedOrder));
        when(securityContextManager.checkSameUser(mockedOrder.getUser().getId())).thenReturn(Boolean.TRUE);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.cancelOrder(id);
        });
        String expectedMessage = "The order is already cancelled.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCancelOrderWithSuccess() {
        Long id = 1L;
        User mockedUserOwner = MockUser.mockEntity(1);
        List<OrderItem> mockedOrderItems = List.of(
            OrderMocker.mockItemEntity(1, 2),
            OrderMocker.mockItemEntity(2, 3)  
        );
        Order mockedOrder = OrderMocker.mockEntity(1, mockedUserOwner, null, mockedOrderItems);
        mockedOrder.setStatus(OrderStatus.CONFIRMED);
        
        when(orderRepository.findById(id)).thenReturn(Optional.of(mockedOrder));
        when(securityContextManager.checkSameUser(mockedOrder.getUser().getId())).thenReturn(Boolean.TRUE);
        when(orderRepository.save(mockedOrder)).thenReturn(mockedOrder);

        OrderResponseDTO output = service.cancelOrder(id);
        assertEquals(1L, output.id());
        assertEquals("CANCELED", output.status());

        ArgumentCaptor<InventoryItem> inventoryItemArgumentCaptor 
            = ArgumentCaptor.forClass(InventoryItem.class);
        verify(inventoryItemRepository, times(2))
            .save(inventoryItemArgumentCaptor.capture());
        List<InventoryItem> capturedInventoryItems = inventoryItemArgumentCaptor.getAllValues();
        assertEquals(2, capturedInventoryItems.size());
        assertEquals(1L, capturedInventoryItems.get(0).getId());
        assertEquals(3, capturedInventoryItems.get(0).getQuantity());
        assertEquals(2L, capturedInventoryItems.get(1).getId());
        assertEquals(5, capturedInventoryItems.get(1).getQuantity());
    }
}
