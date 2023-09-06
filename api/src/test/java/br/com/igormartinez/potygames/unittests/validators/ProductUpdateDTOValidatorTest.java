package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.ProductUpdateDTO;
import jakarta.validation.ConstraintViolation;

public class ProductUpdateDTOValidatorTest extends GenericValidatorTest {
    
    @Test
    void withSuccessWithFieldsFilled() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, 2L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void withSuccessWithFieldsNull() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, 2L, "Lorem Ipsum", null);

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdNull() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(null, 1L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdZero() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(0L, 1L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdNegative() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(-1L, 2L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductTypeNull() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, null, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product type must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductTypeZero() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, 0L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product type must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdProductTypeNegative() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, -22L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of product type must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameNull() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, 2L, null, "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must not be blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameBlank() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, 2L, " ", "Lorem ipsum dolor sit.");

        Set<ConstraintViolation<ProductUpdateDTO>> violations = validator.validate(productDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must not be blank.", violations.iterator().next().getMessage());
    }
}
