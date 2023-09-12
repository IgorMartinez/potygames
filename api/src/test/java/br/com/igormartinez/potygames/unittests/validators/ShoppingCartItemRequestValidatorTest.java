package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.ShoppingCartItemRequestDTO;
import jakarta.validation.ConstraintViolation;

public class ShoppingCartItemRequestValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccess() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(1L, 1);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdNull() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(null, 1);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        assertEquals("The id of inventory item must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdZero() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(0L, 1);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        assertEquals("The id of inventory item must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdNegative() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(-1L, 1);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        assertEquals("The id of inventory item must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNull() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(1L, null);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityZero() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(1L, 0);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNegative() {
        ShoppingCartItemRequestDTO item = new ShoppingCartItemRequestDTO(1L, -10);

        Set<ConstraintViolation<ShoppingCartItemRequestDTO>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be positive number.", violations.iterator().next().getMessage());
    }
}
