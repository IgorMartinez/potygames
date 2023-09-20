package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import jakarta.validation.ConstraintViolation;

public class OrderAddressRequestDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccessWithComplementNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");
        
        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithComplementFilled() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", "Home", "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");
        
        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithStreetNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            null, "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The street must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithStreetBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The street must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNumberNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", null, null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The number must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNumberBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", " ", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The number must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNeighborhoodNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, null, 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The neighborhood must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNeighborhoodBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "  ", 
            "Fruit", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The neighborhood must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithCityNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            null, "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The city must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithCityBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            " ", "São Paulo", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The city must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithStateNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", null, "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The state must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithStateBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", " ", "Brasil", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The state must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithCountryNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", null, "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The country must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithCountryBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "", "00000-000");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The country must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithZipCodeNull() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", null);

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The zip code must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithZipCodeBlank() {
        OrderAddressRequestDTO object = new OrderAddressRequestDTO(
            "Street Apple", "1", null, "Orange", 
            "Fruit", "São Paulo", "Brasil", " ");

        Set<ConstraintViolation<OrderAddressRequestDTO>> violations = validator.validate(object);
        assertEquals(1, violations.size());
        assertEquals("The zip code must be not blank.", violations.iterator().next().getMessage());
    }
}
