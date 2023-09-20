package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import br.com.igormartinez.potygames.mappers.OrderAddressRequestDTOToEntityMapper;
import br.com.igormartinez.potygames.models.OrderAddress;

public class OrderAddressRequestDTOToEntityMapperTest {

    private OrderAddressRequestDTOToEntityMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new OrderAddressRequestDTOToEntityMapper();
    }

    @Test
    void testSuccess() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", "Home", "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        OrderAddress output = mapper.apply(address);
        assertNull(output.getId());
        assertNull(output.getOrder());
        assertNull(output.getBillingAddress());
        assertNull(output.getDeliveryAddress());
        assertEquals("Street Apple", output.getStreet());
        assertEquals("1", output.getNumber());
        assertEquals("Home", output.getComplement());
        assertEquals("Orange", output.getNeighborhood());
        assertEquals("Fruit", output.getCity());
        assertEquals("São Paulo", output.getState());
        assertEquals("Brasil", output.getCountry());
        assertEquals("00000-000", output.getZipCode());
    }
}
