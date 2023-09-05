package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
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
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
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

        // Verify if the generated token is valid
        UserDTO output = 
            given()
                .basePath("/api/v1/user")
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
    @Order(0)
    void testSigninWithoutBody() {
        APIErrorResponse output = 
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
						    .as(APIErrorResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testSigninWithFieldsNullOrBlank() {
        AccountCredentials credentials = new AccountCredentials(null, " ");

        APIErrorResponse output = 
            given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(credentials)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
					.extract()
					    .body()
						    .as(APIErrorResponse.class);

        assertNotNull(output);
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
    @Order(0)
    void testSigninWithWrongEmail() {
        AccountCredentials accountCredentials = 
            new AccountCredentials("wrongemail", TestConfigs.USER_ADMIN_PASSWORD);

        APIErrorResponse output = 
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
						    .as(APIErrorResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid email or password.", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testSigninWithWrongPassword() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(TestConfigs.USER_ADMIN_USERNAME, "wrongpassword");

        APIErrorResponse output = 
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
						    .as(APIErrorResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid email or password.", output.detail());
        assertEquals("/auth/signin", output.instance());
        assertNull(output.errors());
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

        // Test if the generated token is valid
        UserDTO output = 
            given()
                .basePath("/api/v1/user")
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
    @Order(100)
    void testRefreshTokenWithoutHeader() {
        APIErrorResponse output = 
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
                            .as(APIErrorResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Required header 'Authorization' is not present.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testRefreshTokenWithTokenBlank() {
        APIErrorResponse output = 
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
                            .as(APIErrorResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The refresh token must be not blank.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testRefreshTokenWithTokenInvalid() {
        APIErrorResponse output = 
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
                            .as(APIErrorResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid refresh token.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testRefreshTokenWithTokenExpired() {
        APIErrorResponse output = 
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
                            .as(APIErrorResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("Invalid refresh token.", output.detail());
        assertEquals("/auth/refresh", output.instance());
        assertNull(output.errors());
    }
}
