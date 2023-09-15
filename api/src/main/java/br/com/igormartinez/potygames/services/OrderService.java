package br.com.igormartinez.potygames.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.Order;
import br.com.igormartinez.potygames.models.OrderAddress;
import br.com.igormartinez.potygames.models.OrderItem;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.OrderRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final OrderAddressRequestDTOToEntityMapper addressMapper;
    private final OrderEntityToDetailDTOMapper orderMapper;
    private final SecurityContextManager securityContextManager;

    public OrderService(OrderRepository orderRepository, InventoryItemRepository inventoryItemRepository,
            OrderAddressRequestDTOToEntityMapper addressMapper, OrderEntityToDetailDTOMapper orderMapper,
            SecurityContextManager securityContextManager) {
        this.orderRepository = orderRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.addressMapper = addressMapper;
        this.orderMapper = orderMapper;
        this.securityContextManager = securityContextManager;
    }

    /**
     * Get all orders from authenticated user.
     * @return List of all orders with detailed informations.
     */
    public List<OrderDetailResponseDTO> findAllByUser() {
        User user = securityContextManager.getUser();

        return orderRepository.findAllByUserId(user.getId())
            .stream()
            .map(orderMapper)
            .toList();
    }

    /**
     * Get a order from provided id.
     * @param id must be not null and greater than zero.
     * @return A order with detailed informations.
     */
    public OrderDetailResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The order was not found with the given ID."));

        if (!securityContextManager.checkSameUser(order.getUser().getId()))
            throw new UserUnauthorizedException();
        
        return orderMapper.apply(order);
    }

    /**
     * Create a new order.
     * @param request must be not null and already validated.
     * @return New order ID and its status.
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        
        for (OrderItemResquestDTO itemDTO : request.items()) {
            
            InventoryItem item = inventoryItemRepository.findById(itemDTO.idInventoryItem())
                .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not found with the given ID."));
        
            if (item.getQuantity() < itemDTO.quantity())
                throw new ResourceInsufficientException("The order exceeded the quantity in inventory.");

            item.setQuantity(item.getQuantity() - itemDTO.quantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setUnitPrice(item.getPrice());

            inventoryItemRepository.save(item);

            totalPrice = totalPrice.add(item.getPrice());
            orderItems.add(orderItem);
        }

        OrderAddress billingAddress = addressMapper.apply(request.billingAddress());
        billingAddress.setOrder(order);
        billingAddress.setBillingAddress(Boolean.TRUE);
        billingAddress.setDeliveryAddress(Boolean.FALSE);

        OrderAddress deliveryAddress = addressMapper.apply(request.deliveryAddress());
        deliveryAddress.setOrder(order);
        deliveryAddress.setBillingAddress(Boolean.FALSE);
        deliveryAddress.setDeliveryAddress(Boolean.TRUE);

        order.setUser(securityContextManager.getUser());
        order.setOrderAddresses(List.of(billingAddress, deliveryAddress));
        order.setOrderItems(orderItems);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalPrice(totalPrice);

        Order persistedOrder = orderRepository.save(order);
        return new OrderResponseDTO(persistedOrder.getId(), persistedOrder.getStatus().name());
    }

    /**
     * Cancel a existing order.
     * @param id must be not null and greater than zero.
     * @return Canceled order ID and its status.
     */
    @Transactional
    public OrderResponseDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The order was not found with the given ID."));

        if (!securityContextManager.checkSameUser(order.getUser().getId()))
            throw new UserUnauthorizedException();
        
        if (order.getStatus() == OrderStatus.CANCELED)
            throw new RequestValidationException("The order is already cancelled.");

        for (OrderItem orderItem : order.getOrderItems()) {
            InventoryItem item = orderItem.getItem();
            item.setQuantity(item.getQuantity() + orderItem.getQuantity());
            inventoryItemRepository.save(item);
        }

        order.setStatus(OrderStatus.CANCELED);
        Order persistedOrder = orderRepository.save(order);
        return new OrderResponseDTO(persistedOrder.getId(), persistedOrder.getStatus().name());
    }
}
