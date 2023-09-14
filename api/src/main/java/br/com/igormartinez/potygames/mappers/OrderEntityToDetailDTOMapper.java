package br.com.igormartinez.potygames.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.response.OrderAddressResponseDTO;
import br.com.igormartinez.potygames.data.response.OrderDetailResponseDTO;
import br.com.igormartinez.potygames.data.response.OrderItemResponseDTO;
import br.com.igormartinez.potygames.models.Order;
import br.com.igormartinez.potygames.models.OrderAddress;
import br.com.igormartinez.potygames.models.OrderItem;

@Service
public class OrderEntityToDetailDTOMapper implements Function<Order, OrderDetailResponseDTO> {

    private OrderAddressResponseDTO map(OrderAddress address) {
        if (address == null)
            return null;

        return new OrderAddressResponseDTO(
            address.getStreet(), 
            address.getNumber(), 
            address.getComplement(),
            address.getNeighborhood(), 
            address.getCity(), 
            address.getState(), 
            address.getCountry(), 
            address.getZipCode());
    }

    private List<OrderItemResponseDTO> map(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            return null;

        List<OrderItemResponseDTO> itemDTOList = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO(
                item.getItem().getId(), 
                item.getItem().getProduct().getName(), 
                item.getItem().getVersion(), 
                item.getItem().getCondition(), 
                item.getItem().getPrice(), 
                item.getQuantity()
            );
            itemDTOList.add(itemDTO);
        }

        return itemDTOList;
    }

    @Override
    public OrderDetailResponseDTO apply(Order entity) {
        
        OrderAddress billingAddress = null, deliveryAddress = null;
        for (OrderAddress orderAddress : entity.getOrderAddresses()) {
            if (orderAddress.getBillingAddress())
                billingAddress = orderAddress;
            if (orderAddress.getDeliveryAddress())
                deliveryAddress = orderAddress;
        }

        return new OrderDetailResponseDTO(
            entity.getId(), 
            entity.getStatus().name(), 
            entity.getTotalPrice(), 
            map(entity.getOrderItems()), 
            map(billingAddress), 
            map(deliveryAddress)
        );
    }
    
}
