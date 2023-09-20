package br.com.igormartinez.potygames.mocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import br.com.igormartinez.potygames.data.request.OrderItemResquestDTO;
import br.com.igormartinez.potygames.data.request.OrderRequestDTO;
import br.com.igormartinez.potygames.enums.OrderStatus;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.Order;
import br.com.igormartinez.potygames.models.OrderAddress;
import br.com.igormartinez.potygames.models.OrderItem;
import br.com.igormartinez.potygames.models.User;

public class OrderMocker { 

    public static Order mockEntity(int number, User user, List<OrderAddress> addresses, List<OrderItem> items) {
        Order order = new Order();

        if (user == null)
            user = MockUser.mockEntity(number);

        if (addresses == null)
            addresses = List.of(mockAddressEntity(number, order), mockAddressEntity(number+1, order));
        
        if (items == null)
            items = List.of(mockItemEntity(number, order), mockItemEntity(number+1, order));
        
        order.setId(Long.valueOf(number));
        order.setUser(user);
        order.setOrderAddresses(addresses);
        order.setOrderItems(items);

        BigDecimal totalPrice = items
            .stream()
            .map(x -> x.getUnitPrice())
            .reduce((x, y) -> x.add(y))
            .get();
        order.setTotalPrice(totalPrice);
        order.setStatus((number%2==0) ? OrderStatus.CONFIRMED : OrderStatus.CANCELED);

        return order;
    }

    public static OrderItem mockItemEntity(int number, Order order) {
        InventoryItem inventoryItem = InventoryItemMocker.mockEntity(number);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder((order == null) ? (mockEntity(number, null, null, List.of(orderItem))) : (order));
        orderItem.setId(Long.valueOf(number));
        orderItem.setItem(inventoryItem);
        orderItem.setQuantity(inventoryItem.getQuantity());
        orderItem.setUnitPrice(inventoryItem.getPrice());

        return orderItem;
    }

    public static OrderItem mockItemEntity(int number, int quantity) {
        InventoryItem inventoryItem = InventoryItemMocker.mockEntity(number);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(null);
        orderItem.setId(Long.valueOf(number));
        orderItem.setItem(inventoryItem);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(inventoryItem.getPrice());

        return orderItem;
    }
    
    public static OrderAddress mockAddressEntity(int number, Order order) {
        OrderAddress address = new OrderAddress();
        address.setId(Long.valueOf(number));
        address.setOrder((order == null) ? (mockEntity(number, null, List.of(address), null)) : (order));
        address.setBillingAddress((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        address.setDeliveryAddress((number%2==0) ? Boolean.FALSE : Boolean.TRUE);
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

    public static List<Order> mockEntityList(int startNumber, int endNumber, User user) {
        List<Order> orders = new ArrayList<>();
        for (int i=startNumber; i<=endNumber; i++) {
            orders.add(mockEntity(i, user, null, null));
        }
        return orders;
    }

    public static OrderRequestDTO mockRequestDTO(int number) { 
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(Long.valueOf(number), number),
            new OrderItemResquestDTO(Long.valueOf(number+1), number+1)
        );
        OrderAddressRequestDTO billingAddress = mockAddressRequestDTO(number);
        OrderAddressRequestDTO deliveryAddress = mockAddressRequestDTO(number+1);
        
        return new OrderRequestDTO(
            items, 
            billingAddress, 
            deliveryAddress);
    }

    public static OrderRequestDTO mockRequestDTO(List<OrderItemResquestDTO> items, 
        OrderAddressRequestDTO billingAddress, OrderAddressRequestDTO deliveryAddress) {
        
        if (items == null)
            items = List.of(
                new OrderItemResquestDTO(1L, 2),
                new OrderItemResquestDTO(2L, 3)
            );

        if (billingAddress == null)
            billingAddress = mockAddressRequestDTO(1);

        if (deliveryAddress == null)
            deliveryAddress = mockAddressRequestDTO(1);

        return new OrderRequestDTO(items, billingAddress, deliveryAddress);
    }

    public static OrderAddressRequestDTO mockAddressRequestDTO(int number) {
        return new OrderAddressRequestDTO(
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
