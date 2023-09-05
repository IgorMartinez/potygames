package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.UserAddressUpdateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserAddressUpdateDTOValidatorTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSuccessWithFieldsFilled() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            1L, 1L, true, false, 
            "Casa Principal", "Rua das Flores", "123", 
            "Casa 2", "Jardim Primavera", "São Paulo", 
            "SP", "Brasil", "01234-567");

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            1L, 1L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdAddressNull() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            null, 1L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user address must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdAddressZero() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            0L, 1L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user address must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdAddressNegative() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            -22L, 1L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user address must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdUserNull() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            1L, null, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdUserZero() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            1L, 0L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdUserNegative() {
        UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            1L, -55L, null, null, 
            null, null, null, 
            null, null, null, 
            null, null, null);

        Set<ConstraintViolation<UserAddressUpdateDTO>> violations = validator.validate(addressDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be a positive number.", violations.iterator().next().getMessage());
    }
}
