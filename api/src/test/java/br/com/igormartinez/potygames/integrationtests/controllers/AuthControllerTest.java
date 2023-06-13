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

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerTest extends AbstractIntegrationTest {

    private static Token token;

    @Test
    @Order(1)
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
					.statusCode(200)
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
    @Order(2)
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
					.statusCode(200)
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
    
}
