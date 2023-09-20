package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Test methodology:
 * All methods in this controller should be accessed without authentication.
 * Verify all possibilities in signup, signin and refreshToken methods with a newly created user.
 * Finally, remove the user created for testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static String BASE_PATH = "/auth";

    private static Long CUSTOMER_ID; // defined in testSignupWithSuccess()  
    private static String CUSTOMER_EMAIL = "authcontroller@customer.test";
	private static String CUSTOMER_PASSWORD = "securedpassword";
    private static String CUSTOMER_NAME = "Auth Controller Test";
    private static LocalDate CUSTOMER_BIRTH_DATE = LocalDate.of(1996,9,20);
    private static String CUSTOMER_DOCUMENT_NUMBER = "023.009.020-00";
    private static String CUSTOMER_PHONE_NUMBER = "+5500987654321";
    private static Token CUSTOMER_TOKEN; // testSigninWithSuccess()

    @BeforeAll
    void setup() {
        specification = new RequestSpecBuilder()
			.setBasePath(BASE_PATH)
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.build();
    }

    @Test
    @Order(0)
    void testSignupWithoutBody() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                .when()
                    .post("/signup")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                        .extract()
                            .body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/auth/signup", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testSignupWithFieldsInvalid() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "", 
                CUSTOMER_PASSWORD, 
                CUSTOMER_NAME, 
                CUSTOMER_BIRTH_DATE, 
                CUSTOMER_DOCUMENT_NUMBER,
                CUSTOMER_PHONE_NUMBER);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(user)
                .when()
                    .post("/signup")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                        .extract()
                            .body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/auth/signup", output.instance());
        assertEquals(1, output.errors().size());
        assertEquals("The email must be not blank.", output.errors().get("email"));
    }

    @Test
    @Order(0)
    void testSignupWithUserAlreadyExists() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                TestConfigs.USER_ADMIN_EMAIL, 
                CUSTOMER_PASSWORD, 
                CUSTOMER_NAME, 
                CUSTOMER_BIRTH_DATE, 
                CUSTOMER_DOCUMENT_NUMBER,
                CUSTOMER_PHONE_NUMBER);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(user)
                .when()
                    .post("/signup")
                .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                        .extract()
                            .body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Conflict", output.title());
        assertEquals(HttpStatus.CONFLICT.value(), output.status());
        assertEquals("The email is already in use.", output.detail());
        assertEquals("/auth/signup", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testSignupWithSuccess() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD, 
                CUSTOMER_NAME, 
                CUSTOMER_BIRTH_DATE, 
                CUSTOMER_DOCUMENT_NUMBER,
                CUSTOMER_PHONE_NUMBER);

        UserDTO output = 
            given()
                .spec(specification)
                    .body(user)
                .when()
                    .post("/signup")
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .body()
                                .as(UserDTO.class);
        
        assertTrue(output.id() > 0);
        assertEquals(CUSTOMER_EMAIL, output.email());
        assertEquals(CUSTOMER_NAME, output.name());
        assertEquals(CUSTOMER_BIRTH_DATE, output.birthDate());
        assertEquals(CUSTOMER_DOCUMENT_NUMBER, output.documentNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));

        CUSTOMER_ID = output.id();
    }

    @Test
    @Order(100)
    void testSigninWithoutBody() {
        APIErrorResponse output = 
            given()
                .spec(specification)
				.when()
				    .post("/signin")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
					.extract()
					    .body()
						    .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testSigninWithFieldsNullOrBlank() {
        AccountCredentials accountCredentials = new AccountCredentials(null, " ");

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(accountCredentials)
				.when()
				    .post("/signin")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
					.extract()
					    .body()
						    .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertEquals(2, output.errors().size());
        assertEquals("The username must be not blank.", output.errors().get("username"));
        assertEquals("The password must be not blank.", output.errors().get("password"));
    }

    @Test
    @Order(100)
    void testSigninWithWrongEmail() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("wrongemail", TestConfigs.USER_ADMIN_PASSWORD);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(accountCredentials)
				.when()
				    .post("/signin")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
					.extract()
					    .body()
						    .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid email or password.", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testSigninWithWrongPassword() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(TestConfigs.USER_ADMIN_EMAIL, "wrongpassword");

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(accountCredentials)
				.when()
				    .post("/signin")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
					.extract()
					    .body()
						    .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid email or password.", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testSigninWithSuccess() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(TestConfigs.USER_ADMIN_EMAIL, TestConfigs.USER_ADMIN_PASSWORD);

        CUSTOMER_TOKEN = 
            given()
                .spec(specification)
				.body(accountCredentials)
				.when()
				    .post("/signin")
				.then()
					.statusCode(HttpStatus.OK.value())
					.extract()
					    .body()
						    .as(Token.class);

        assertNotNull(CUSTOMER_TOKEN.getUsername());
        assertEquals(TestConfigs.USER_ADMIN_EMAIL, CUSTOMER_TOKEN.getUsername());
        assertNotNull(CUSTOMER_TOKEN.getAuthenticated());
        assertTrue(CUSTOMER_TOKEN.getAuthenticated());
        assertNotNull(CUSTOMER_TOKEN.getCreated());
        assertNotNull(CUSTOMER_TOKEN.getExpiration());
        assertNotNull(CUSTOMER_TOKEN.getAccessToken());
        assertNotNull(CUSTOMER_TOKEN.getRefreshToken());

        // Verify if the generated token is valid
        UserDTO output = 
            given()
                .basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + CUSTOMER_TOKEN.getAccessToken())
                    .pathParam("id", 1)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertEquals(TestConfigs.USER_ADMIN_EMAIL, output.email());
    }
    
    @Test
    @Order(200)
    void testRefreshTokenWithoutHeader() {
        APIErrorResponse output = 
            given()
                .spec(specification)
				.when()
				    .put("/refresh")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                        .body()
                            .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Required header 'Authorization' is not present.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(200)
    void testRefreshTokenWithTokenBlank() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "")
				.when()
				    .put("/refresh")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                        .body()
                            .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The refresh token must be not blank.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(200)
    void testRefreshTokenWithTokenInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "aaaaaa")
				.when()
				    .put("/refresh")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract()
                        .body()
                            .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid refresh token.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(200)
    void testRefreshTokenWithTokenExpired() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + TestConfigs.EXPIRED_REFRESH_TOKEN)
				.when()
				    .put("/refresh")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract()
                        .body()
                            .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid refresh token.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(200)
    void testRefreshTokenWithSuccess() {
        CUSTOMER_TOKEN = 
            given()
                .spec(specification)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + CUSTOMER_TOKEN.getRefreshToken())
				.when()
				    .put("/refresh")
				.then()
					.statusCode(HttpStatus.OK.value())
                    .extract()
                        .body()
                            .as(Token.class);
        
        assertNotNull(CUSTOMER_TOKEN.getUsername());
        assertEquals(TestConfigs.USER_ADMIN_EMAIL, CUSTOMER_TOKEN.getUsername());
        assertNotNull(CUSTOMER_TOKEN.getAuthenticated());
        assertTrue(CUSTOMER_TOKEN.getAuthenticated());
        assertNotNull(CUSTOMER_TOKEN.getCreated());
        assertNotNull(CUSTOMER_TOKEN.getExpiration());
        assertNotNull(CUSTOMER_TOKEN.getAccessToken());
        assertNotNull(CUSTOMER_TOKEN.getRefreshToken());

        // Test if the generated token is valid
        UserDTO output = 
            given()
                .basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + CUSTOMER_TOKEN.getAccessToken())
                    .pathParam("id", 1)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertEquals(TestConfigs.USER_ADMIN_EMAIL, output.email());
    }

    @Test
	@Order(1000)
	void removeUserTest() {
		given()
            .basePath("/api/v1/user")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + CUSTOMER_TOKEN.getAccessToken())
				.pathParam("id", CUSTOMER_ID)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}
}
