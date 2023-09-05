package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.UserPersonalInformationDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserPersonalInformationDTOValidatorTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSuccessWithAllFieldsFilled() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, "Lorem Ipsum", LocalDate.of(1998, 9, 5), 
            "000.000.000-00", "(00) 00000-0000");

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, "Lorem Ipsum", null, 
            null, null);

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithIdNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            null, "Lorem Ipsum", null, 
            null, null);

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be provided.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdZero() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            0L, "Lorem Ipsum", null, 
            null, null);

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithIdNegative() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            -12L, "Lorem Ipsum", null, 
            null, null);

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size());
        assertEquals("The id of user must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameNull() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, null, null, 
            null, null);

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameBlank() {
        UserPersonalInformationDTO userDTO = new UserPersonalInformationDTO(
            1L, "  ", null, 
            null, null);

        Set<ConstraintViolation<UserPersonalInformationDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must be not blank.", violations.iterator().next().getMessage());
    }
}
