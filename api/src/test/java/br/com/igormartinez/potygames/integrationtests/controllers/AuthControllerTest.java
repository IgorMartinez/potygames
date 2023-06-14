package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerTest extends AbstractIntegrationTest {

    private static Token token;

    @Test
    @Order(0)
    void testSignin() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(TestConfigs.USER_ADMIN_USERNAME, TestConfigs.USER_ADMIN_PASSWORD);

        token = 
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
        assertNotNull(token.getUsername());
        assertEquals(TestConfigs.USER_ADMIN_USERNAME, token.getUsername());
        assertNotNull(token.getAuthenticated());
        assertTrue(token.getAuthenticated());
        assertNotNull(token.getCreated());
        assertNotNull(token.getExpiration());
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(1)
    void testTokenGeneratedInSignin() {
        UserDTO output = 
            given()
                .basePath("/api/user/v1")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getAccessToken())
                    .pathParam("id", 1)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertNotNull(output.email());
        assertEquals(TestConfigs.USER_ADMIN_USERNAME, output.email());
    }

    @Test
    void testSigninWithoutAccountCredentials() {
        ExceptionResponse output = 
            given()
				.basePath("/auth/signin")
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
        assertEquals("/auth/signin", output.getInstance());
    }

    @Test
    void testSigninWithWrongEmail() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("wrongemail", TestConfigs.USER_ADMIN_PASSWORD);

        ExceptionResponse output = 
            given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(accountCredentials)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
					.extract()
					    .body()
						    .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("Invalid email or password", output.getDetail());
        assertEquals("/auth/signin", output.getInstance());
    }

    @Test
    void testSigninWithWrongPassword() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(TestConfigs.USER_ADMIN_USERNAME, "wrongpassword");

        ExceptionResponse output = 
            given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(accountCredentials)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
					.extract()
					    .body()
						    .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("Invalid email or password", output.getDetail());
        assertEquals("/auth/signin", output.getInstance());
    }

    @Test
    @Order(100)
    void testRefreshToken() {
        token = 
            given()
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
				.when()
				    .put()
				.then()
					.statusCode(HttpStatus.OK.value())
                    .extract()
                        .body()
                            .as(Token.class);
        
        assertNotNull(token);
        assertNotNull(token.getUsername());
        assertEquals(TestConfigs.USER_ADMIN_USERNAME, token.getUsername());
        assertNotNull(token.getAuthenticated());
        assertTrue(token.getAuthenticated());
        assertNotNull(token.getCreated());
        assertNotNull(token.getExpiration());
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(101)
    void testTokenGeneratedInRefresh() {
        UserDTO output = 
            given()
                .basePath("/api/user/v1")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getAccessToken())
                    .pathParam("id", 1)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertNotNull(output.email());
        assertEquals(TestConfigs.USER_ADMIN_USERNAME, output.email());
    }
    
    @Test
    void testRefreshTokenWithoutHeader() {
        ExceptionResponse output = 
            given()
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .put()
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                        .body()
                            .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Required header 'Authorization' is not present.", output.getDetail());
        assertEquals("/auth/refresh", output.getInstance());
    }

    @Test
    void testRefreshTokenWithTokenBlank() {
        ExceptionResponse output = 
            given()
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "")
				.when()
				    .put()
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
        assertEquals("/auth/refresh", output.getInstance());
    }

    @Test
    void testRefreshTokenWithTokenInvalid() {
        ExceptionResponse output = 
            given()
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "aaaaaa")
				.when()
				    .put()
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract()
                        .body()
                            .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("Invalid token", output.getDetail());
        assertEquals("/auth/refresh", output.getInstance());
    }

    @Test
    void testRefreshTokenWithTokenExpired() {
        ExceptionResponse output = 
            given()
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + TestConfigs.EXPIRED_REFRESH_TOKEN)
				.when()
				    .put()
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract()
                        .body()
                            .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("Invalid token", output.getDetail());
        assertEquals("/auth/refresh", output.getInstance());
    }
}
