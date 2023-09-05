package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserRegistrationDTOValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSuccessWithAllFieldsFilled() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "lorem@ipsum.test", "securedpassword", "Lorem Ipsum", 
            LocalDate.of(1998, 9, 5), "000.000.000-00", 
            "(00) 00000-0000");

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSuccessWithFieldsNull() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "lorem@ipsum.test", "securedpassword", "Lorem Ipsum", 
            null, null, null);

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithEmailNull() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            null, "securedpassword", "Lorem Ipsum", 
            null, null, null);
        
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertEquals(1, violations.size());
        assertEquals("The email must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithEmailBlank() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            " ", "securedpassword", "Lorem Ipsum", 
            null, null, null);
        
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertEquals(1, violations.size());
        assertEquals("The email must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPasswordNull() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "lorem@ipsum.test", null, "Lorem Ipsum", 
            null, null, null);
        
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertEquals(1, violations.size());
        assertEquals("The password must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPasswordBlank() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "lorem@ipsum.test", "", "Lorem Ipsum", 
            null, null, null);
        
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertEquals(1, violations.size());
        assertEquals("The password must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameNull() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "lorem@ipsum.test", "securedpassword", null, 
            null, null, null);
        
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithNameBlank() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "lorem@ipsum.test", "securedpassword", " ", 
            null, null, null);
        
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
        assertEquals(1, violations.size());
        assertEquals("The name must be not blank.", violations.iterator().next().getMessage());
    }
}
