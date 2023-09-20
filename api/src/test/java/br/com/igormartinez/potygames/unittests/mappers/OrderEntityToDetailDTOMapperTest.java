package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.response.OrderDetailResponseDTO;
import br.com.igormartinez.potygames.data.response.OrderItemResponseDTO;
import br.com.igormartinez.potygames.mappers.OrderEntityToDetailDTOMapper;
import br.com.igormartinez.potygames.mocks.OrderMocker;
import br.com.igormartinez.potygames.models.Order;

public class OrderEntityToDetailDTOMapperTest {
    
    private OrderEntityToDetailDTOMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new OrderEntityToDetailDTOMapper();
    }

    @Test
    void testSuccess() {
        Order order = OrderMocker.mockEntity(1, null, null, null);

        OrderDetailResponseDTO output = mapper.apply(order);
        assertEquals(1L, output.id());
        assertEquals("CANCELED", output.status());
        assertEquals(0, output.totalPrice().compareTo(new BigDecimal("4.98")));

        assertEquals(2, output.items().size());

        OrderItemResponseDTO outputItemPosition0 = output.items().get(0);
        assertEquals(1L, outputItemPosition0.id());
        assertEquals("Product name 1", outputItemPosition0.name());
        assertEquals("Version 1", outputItemPosition0.version());
        assertEquals("Condition 1", outputItemPosition0.condition());
        assertEquals(0, outputItemPosition0.unitPrice().compareTo(new BigDecimal("1.99")));
        assertEquals(1, outputItemPosition0.quantity());

        OrderItemResponseDTO outputItemPosition1 = output.items().get(1);
        assertEquals(2L, outputItemPosition1.id());
        assertEquals("Product name 2", outputItemPosition1.name());
        assertEquals("Version 2", outputItemPosition1.version());
        assertEquals("Condition 2", outputItemPosition1.condition());
        assertEquals(0, outputItemPosition1.unitPrice().compareTo(new BigDecimal("2.99")));
        assertEquals(2, outputItemPosition1.quantity());

        assertEquals("Street 2", output.billingAddress().street());
        assertEquals("Number 2", output.billingAddress().number());
        assertEquals("Complement 2", output.billingAddress().complement());
        assertEquals("Neighborhood 2", output.billingAddress().neighborhood());
        assertEquals("City 2", output.billingAddress().city());
        assertEquals("State 2", output.billingAddress().state());
        assertEquals("Country 2", output.billingAddress().country());
        assertEquals("00000-002", output.billingAddress().zipCode());

        assertEquals("Street 1", output.deliveryAddress().street());
        assertEquals("Number 1", output.deliveryAddress().number());
        assertEquals("Complement 1", output.deliveryAddress().complement());
        assertEquals("Neighborhood 1", output.deliveryAddress().neighborhood());
        assertEquals("City 1", output.deliveryAddress().city());
        assertEquals("State 1", output.deliveryAddress().state());
        assertEquals("Country 1", output.deliveryAddress().country());
        assertEquals("00000-001", output.deliveryAddress().zipCode());
    }
}
