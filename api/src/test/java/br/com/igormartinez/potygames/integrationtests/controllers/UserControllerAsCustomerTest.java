package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerAsCustomerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

	private static Long USER_ID; // defined in testSignup() 
    private static String USER_EMAIL = "usercontroller@customer.test";
	private static String USER_PASSWORD = "securedpassword";
    private static String USER_NAME = "User Controller Test";
    private static LocalDate USER_BIRTH_DATE = LocalDate.of(1996,7,23);
    private static String USER_DOCUMENT_NUMBER = "023.007.023-00";
    private static String USER_PHONE_NUMBER = "+5500987654321";

    @Test
    @Order(0)
    void testSignup() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                USER_EMAIL, 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        UserDTO customerDTO = 
            given()
                .basePath("/api/v1/user/signup")
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
        assertEquals(USER_EMAIL, customerDTO.email());
        assertEquals(USER_NAME, customerDTO.name());
        assertEquals(USER_BIRTH_DATE, customerDTO.birthDate());
        assertEquals(USER_DOCUMENT_NUMBER, customerDTO.documentNumber());
        assertTrue(customerDTO.accountNonExpired());
        assertTrue(customerDTO.accountNonLocked());
        assertTrue(customerDTO.credentialsNonExpired());
        assertTrue(customerDTO.enabled());
        assertEquals(1, customerDTO.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), customerDTO.permissions().get(0));

        USER_ID = customerDTO.id();
    }

    @Test
    @Order(0)
    void testSignupWithoutBody() {
        ExceptionResponse output = 
            given()
                .basePath("/api/v1/user/signup")
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
        assertEquals("/api/v1/user/signup", output.getInstance());
    }

    @Test
    @Order(0)
    void testSignupWithEmailBlank() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "", 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        ExceptionResponse output = 
            given()
                .basePath("/api/v1/user/signup")
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
        assertEquals("/api/v1/user/signup", output.getInstance());
    }

    @Test
    @Order(0)
    void testSignupWithUserAlreadyExists() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "rlayzell0@pen.io", 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        ExceptionResponse output = 
            given()
                .basePath("/api/v1/user/signup")
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
        assertEquals("/api/v1/user/signup", output.getInstance());
    }

    @Test
    @Order(1)
    void authentication() {
        AccountCredentials accountCredentials = new AccountCredentials(USER_EMAIL, USER_PASSWORD);

        String accessToken = 
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
								.as(Token.class)
								    .getAccessToken();

		specification = new RequestSpecBuilder()
			.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
			.setBasePath("/api/v1/user")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(2)
    void testFindAll() {
        ExceptionResponse exceptionResponse =
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);

        assertNotNull(exceptionResponse);
        assertEquals("about:blank", exceptionResponse.getType());
        assertEquals("Unauthorized", exceptionResponse.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exceptionResponse.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource", exceptionResponse.getDetail());
        assertEquals("/api/v1/user", exceptionResponse.getInstance());
    }

    @Test
    @Order(10)
    void testFindByIdWithSameUser() {
        UserDTO output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", USER_ID)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertEquals(USER_ID, output.id());
        assertEquals(USER_EMAIL, output.email());
        assertEquals(USER_NAME, output.name());
        assertEquals(USER_BIRTH_DATE, output.birthDate());
        assertEquals(USER_DOCUMENT_NUMBER, output.documentNumber());
        assertEquals(USER_PHONE_NUMBER, output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));
    }

    @Test
    @Order(10)
    void testFindByIdWithOtherUser() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", USER_ID+1)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/v1/user/"+(USER_ID+1), output.getInstance());
    }

    @Test
    @Order(10)
    void testFindByIdWithIdInvalid() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 0)
                .when()
                    .get("{id}")
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
        assertEquals("/api/v1/user/0", output.getInstance());
    }

    @Test
    @Order(20)
    void testUpdatePersonalInformationWithSameUser() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                USER_ID, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        UserPersonalInformationDTO output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", USER_ID)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserPersonalInformationDTO.class);
        
        assertNotNull(output);
        assertEquals(USER_ID, output.id());
        assertEquals("Test Name Updated", output.name());
        assertEquals(LocalDate.of(2023,06,14), output.birthDate());
        assertEquals("000.000.000-00", output.documentNumber());
        assertEquals("+5511999990000", output.phoneNumber());
    }

    @Test
    @Order(20)
    void testUpdatePersonalInformationWithOtherUser() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                USER_ID+1, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", USER_ID+1)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/v1/user/"+(USER_ID+1)+"/personal-information", output.getInstance());
    }

    @Test
    @Order(20)
    void testUpdatePersonalInformationWithIdInvalid() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                Long.valueOf(3), 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 0)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
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
        assertEquals("/api/v1/user/0/personal-information", output.getInstance());
    }

    @Test
    @Order(20)
    void testUpdatePersonalInformationWithBodyEmpty() {

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", USER_ID)
                .when()
                    .put("{id}/personal-information")
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
        assertEquals("/api/v1/user/"+USER_ID+"/personal-information", output.getInstance());
    }

    @Test
    @Order(30)
    void testDeleteWithOtherUser() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", USER_ID+1)
                .when()
                    .delete("{id}")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/v1/user/"+(USER_ID+1), output.getInstance());
    }

    @Test
    @Order(30)
    void testDeleteWithIdInvalid() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", -10)
                .when()
                    .delete("{id}")
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
        assertEquals("/api/v1/user/-10", output.getInstance());
    }

    @Test
    @Order(31)
    void testDeleteWithSameUser() {
        given()
			.spec(specification)
			    .contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", USER_ID)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
			.extract()
				.body()
					.asString();
    }
}
