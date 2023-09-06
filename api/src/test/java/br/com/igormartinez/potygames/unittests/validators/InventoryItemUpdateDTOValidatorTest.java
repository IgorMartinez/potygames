package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.InventoryItemUpdateDTO;
import jakarta.validation.ConstraintViolation;

public class InventoryItemUpdateDTOValidatorTest extends GenericValidatorTest {
    @Test
    void testSuccessWithFieldsFilled() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", null, null, 0);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdNull() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            null, 1L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdZero() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            0L, 1L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdNegative() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            -10L, 1L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductNull() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, null, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductZero() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 0L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductNegative() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, -10L, "AA2", "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithVersionNull() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, null, "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The version must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithVersionBlank() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, null, "New", new BigDecimal("1.99"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The version must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPrice11IntegerDigits() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", "New", new BigDecimal("12345678901.01"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The price must have up to 10 integer digits and 2 decimal digits of precision.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPrice3DecimalDigits() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", "New", new BigDecimal("1234567890.012"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The price must have up to 10 integer digits and 2 decimal digits of precision.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPriceNegative() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", "New", new BigDecimal("-0.01"), 10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The price must be null, zero or positive.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNull() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", "New", new BigDecimal("0.01"), null);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithQuantityNegative() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            1L, 1L, "AA2", "New", new BigDecimal("0.01"), -10);

        Set<ConstraintViolation<InventoryItemUpdateDTO>> violations = validator.validate(itemDTO);
        assertEquals(1, violations.size());
        assertEquals("The quantity must be zero or positive.", violations.iterator().next().getMessage());
    }
}
