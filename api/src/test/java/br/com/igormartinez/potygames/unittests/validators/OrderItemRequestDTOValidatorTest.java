package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.OrderItemResquestDTO;
import jakarta.validation.ConstraintViolation;

public class OrderItemRequestDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccess() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(1L, 2);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdNull() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(null, 2);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The id of inventory item must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdZero() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(0L, 1);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The id of inventory item must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdNegative() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(-1L, 1);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The id of inventory item must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNull() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(1L, null);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityZero() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(1L, 0);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNegative() {
        OrderItemResquestDTO object = new OrderItemResquestDTO(1L, -10);

        Set<ConstraintViolation<OrderItemResquestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be positive number.", violations.iterator().next().getMessage());
    }
}
