package br.com.igormartinez.potygames.unittests.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.request.AccountCredentials;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AccountCredentialsValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSuccess() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("lorem@ipsum.com", "1234");
        
        Set<ConstraintViolation<AccountCredentials>> violations = validator.validate(accountCredentials);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWithUsernameNull() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(null, "1234");
        
        Set<ConstraintViolation<AccountCredentials>> violations = validator.validate(accountCredentials);
        assertEquals(1, violations.size());
        assertEquals("The username must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithUsernameBlank() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("  ", "1234");
        
        Set<ConstraintViolation<AccountCredentials>> violations = validator.validate(accountCredentials);
        assertEquals(1, violations.size());
        assertEquals("The username must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPasswordNull() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("lorem@ipsum.com", null);
        
        Set<ConstraintViolation<AccountCredentials>> violations = validator.validate(accountCredentials);
        assertEquals(1, violations.size());
        assertEquals("The password must be not blank.", violations.iterator().next().getMessage());
    }

    @Test
    void testWithPasswordBlank() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("lorem@ipsum.com", "  ");
        
        Set<ConstraintViolation<AccountCredentials>> violations = validator.validate(accountCredentials);
        assertEquals(1, violations.size());
        assertEquals("The password must be not blank.", violations.iterator().next().getMessage());
    }
}
