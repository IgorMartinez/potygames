package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import br.com.igormartinez.potygames.models.OrderAddress;

@Service
public class OrderAddressRequestDTOToEntityMapper implements Function<OrderAddressRequestDTO, OrderAddress> {

    @Override
    public OrderAddress apply(OrderAddressRequestDTO dto) {
        OrderAddress entity = new OrderAddress();
        entity.setStreet(dto.street());
        entity.setNumber(dto.number());
        entity.setComplement(dto.complement());
        entity.setNeighborhood(dto.neighborhood());
        entity.setCity(dto.city());
        entity.setState(dto.state());
        entity.setCountry(dto.country());
        entity.setZipCode(dto.zipCode());

        return entity;
    }
    
}
