package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.InventoryItemCreateDTO;
import jakarta.validation.ConstraintViolation;

public class InventoryItemCreateDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccessWithFieldsFilled() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", null, null, 0);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdProductNull() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            null, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductZero() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            0L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductNegative() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            -10L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithVersionNull() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, null, "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The version must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithVersionBlank() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, null, "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The version must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPrice11IntegerDigits() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", "New", new BigDecimal("12345678901.01"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The price must have up to 10 integer digits and 2 decimal digits of precision.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPrice3DecimalDigits() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", "New", new BigDecimal("1234567890.012"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The price must have up to 10 integer digits and 2 decimal digits of precision.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPriceNegative() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", "New", new BigDecimal("-0.01"), 10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The price must be null, zero or positive.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNull() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", "New", new BigDecimal("0.01"), null);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNegative() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "AA2", "New", new BigDecimal("0.01"), -10);

        Set<ConstraintViolation<InventoryItemCreateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be zero or positive.", violations.iterator().next().getMessage());
    }
}
