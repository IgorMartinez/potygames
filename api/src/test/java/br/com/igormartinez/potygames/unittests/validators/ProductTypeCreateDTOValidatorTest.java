package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.ProductTypeCreateDTO;
import jakarta.validation.ConstraintViolation;

public class ProductTypeCreateDTOValidatorTest extends GenericValidatorTest {

    @Test
    void testSuccess() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("pokemon-cards", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeCreateDTO>> violations = validator.validate(productTypeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithKeyWordNull() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO(null, "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeCreateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The keyword must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithKeyWordBlank() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("  ", "Pokémon Cards");

        Set<ConstraintViolation<ProductTypeCreateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The keyword must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithDescriptonNull() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("pokemon-cards", null);

        Set<ConstraintViolation<ProductTypeCreateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The description must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithDescriptonBlank() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("pokemon-cards", "");

        Set<ConstraintViolation<ProductTypeCreateDTO>> violations = validator.validate(productTypeDTO);
        assertEquals(1, violations.size());
        assertEquals("The description must not be blank.", violations.iterator().next().getMessage());
    }
}
