package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.UserAddressCreateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserAddressCreateDTOValidatorTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSuccessWithFieldsFilled() {
        UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            1L, true, false, 
            "Casa Principal", "Rua das Flores", "123", 
            "Casa 2", "Jardim Primavera", "São Paulo", 
            "SP", "Brasil", "01234-567");

        Set<ConstraintViolation<UserAddressCreateDTO>> violations = validator.validate(addressDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            1L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressCreateDTO>> violations = validator.validate(addressDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdUserNull() {
        UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            null, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressCreateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdUserZero() {
        UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            0L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressCreateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdUserNegative() {
        UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            -55L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressCreateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be a positive number.", violations.iterator().next().getMessage());
    }
}
