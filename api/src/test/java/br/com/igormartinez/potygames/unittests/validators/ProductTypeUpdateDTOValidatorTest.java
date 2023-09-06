package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.ProductTypeUpdateDTO;
import jakarta.validation.ConstraintViolation;

public class ProductTypeUpdateDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccess() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(1L, "pokemon-cards", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdNull() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(null, "pokemon-cards", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdZero() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(0L, "pokemon-cards", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdNegative() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(-1L, "pokemon-cards", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithKeyWordNull() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(1L, null, "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The keyword must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithKeyWordBlank() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(1L, "  ", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The keyword must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithDescriptonNull() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(1L, "pokemon-cards", null);

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The description must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithDescriptonBlank() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(1L, "pokemon-cards", "");

        Set<ConstraintViolation<ProductTypeUpdateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The description must not be blank.", violations.iterator().next().getMessage());
    }
}
