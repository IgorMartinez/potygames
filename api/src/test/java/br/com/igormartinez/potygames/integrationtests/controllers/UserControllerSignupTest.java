package br.com.igormartinez.potygames.integrationtests.controllers;

import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerSignupTest extends AbstractIntegrationTest {
    
    @Test
    @Order(0)
    void testSignup() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "signupnewcustomer@test.com", 
                "testsignup", 
                "Signup Test", 
                LocalDate.of(2023,6,13), 
                "023.006.013-00");

        UserDTO customerDTO = 
            given()
                .basePath("/api/user/v1/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .body()
                                .as(UserDTO.class);
        
        assertNotNull(customerDTO);
        assertTrue(customerDTO.id() > 0);
        assertEquals("signupnewcustomer@test.com", customerDTO.email());
        assertEquals("Signup Test", customerDTO.name());
        assertEquals(LocalDate.of(2023,6,13), customerDTO.birthDate());
        assertEquals("023.006.013-00", customerDTO.documentNumber());
        assertTrue(customerDTO.accountNonExpired());
        assertTrue(customerDTO.accountNonLocked());
        assertTrue(customerDTO.credentialsNonExpired());
        assertTrue(customerDTO.enabled());
        assertEquals(1, customerDTO.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), customerDTO.permissions().get(0));
    }

    @Test
    @Order(1)
    void testAuthenticationWithNewUser() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("signupnewcustomer@test.com", "testsignup");

        Token token = 
			given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
				    .body(accountCredentials)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.as(Token.class);
        
        assertNotNull(token);
        assertEquals("signupnewcustomer@test.com", token.getUsername());
        assertTrue(token.getAuthenticated());
    }

    @Test
    void testSignupWithoutBody() {
        ExceptionResponse output = 
            given()
                .basePath("/api/user/v1/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                        .extract()
                            .body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Failed to read request", output.getDetail());
        assertEquals("/api/user/v1/signup", output.getInstance());
    }

    @Test
    void testSignupWithEmailBlank() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "", 
                "testsignup", 
                "Signup Test", 
                LocalDate.of(2023,6,13), 
                "023.006.013-00");

        ExceptionResponse output = 
            given()
                .basePath("/api/user/v1/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                        .extract()
                            .body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/user/v1/signup", output.getInstance());
    }

    @Test
    void testSignupWithUserAlreadyExists() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "rlayzell0@pen.io", 
                "1234", 
                "User already exists", 
                LocalDate.of(2023,6,14), 
                "000.000.000-00");

        ExceptionResponse output = 
            given()
                .basePath("/api/user/v1/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                        .extract()
                            .body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Conflict", output.getTitle());
        assertEquals(HttpStatus.CONFLICT.value(), output.getStatus());
        assertEquals("Request could not be processed because the resource already exists", output.getDetail());
        assertEquals("/api/user/v1/signup", output.getInstance());
    }
}
