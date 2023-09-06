package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.ProductCreateDTO;
import jakarta.validation.ConstraintViolation;

public class ProductCreateDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void testSuccessWithFieldsFilled() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, "Lorem Ipsum", null);

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdProductTypeNull() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(null, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product type must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductTypeZero() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(0L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product type must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductTypeNegative() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(-44L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product type must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameNull() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, null, "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameBlank() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, " ", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductCreateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must not be blank.", violations.iterator().next().getMessage());
    }
}
