package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import br.com.igormartinez.potygames.data.request.OrderItemResquestDTO;
import br.com.igormartinez.potygames.data.request.OrderRequestDTO;
import jakarta.validation.ConstraintViolation;

public class OrderRequestDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccess() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        List<OrderItemResquestDTO> items = new ArrayList<>();
        items.add(new OrderItemResquestDTO(1L, 2));
        
        OrderRequestDTO object = new OrderRequestDTO(items, address, address);

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(object);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithItemsNull() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        OrderRequestDTO object = new OrderRequestDTO(null, address, address);

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The items of order must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithItemsEmpty() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        List<OrderItemResquestDTO> items = new ArrayList<>();

        OrderRequestDTO object = new OrderRequestDTO(items, address, address);

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The items of order must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithItemsDuplicated() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        List<OrderItemResquestDTO> items = new ArrayList<>();

        OrderRequestDTO object = new OrderRequestDTO(items, address, address);
        items.add(new OrderItemResquestDTO(1L, 2));
        items.add(new OrderItemResquestDTO(2L, 3));
        items.add(new OrderItemResquestDTO(1L, 3));

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The list of items cannot have duplicated elements.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithBillingAddressNull() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        List<OrderItemResquestDTO> items = new ArrayList<>();

        OrderRequestDTO object = new OrderRequestDTO(items, null, address);
        items.add(new OrderItemResquestDTO(1L, 2));
        items.add(new OrderItemResquestDTO(2L, 3));

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The billing address of order must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithDeliveryAddressNull() {
        OrderAddressRequestDTO address = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        List<OrderItemResquestDTO> items = new ArrayList<>();

        OrderRequestDTO object = new OrderRequestDTO(items, address, null);
        items.add(new OrderItemResquestDTO(1L, 2));
        items.add(new OrderItemResquestDTO(2L, 3));

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The delivery address of order must be provided.", violations.iterator().next().getMessage());
    }
}
