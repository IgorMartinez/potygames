package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.ShoppingCartItemRequestDTO;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.ShoppingCartItemResponseDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class ShoppingCartControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static Long CUSTOMER_ID; // signupAndAuthenticationAsCustomer()
    private static String CUSTOMER_EMAIL = "shoppingcartcontroller@customer.test";
	private static String CUSTOMER_PASSWORD = "securedpassword";

    @Test
    @Order(0)
    void testFindAllByUserAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
				.when()
				    .get("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testAddItemToCartAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testUpdateItemInCartAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
                    .pathParam("inventory-item-id", 2)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testRemoveItemFromCartAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
                    .pathParam("inventory-item-id", 2)
				.when()
				    .delete("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(99)
    void signupAndAuthenticationAsCustomer() {
		UserRegistrationDTO user = 
            new UserRegistrationDTO(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD, 
                "Signup Test", 
                LocalDate.of(1996,9,12), 
                "012.009.023-00",
				null);

        CUSTOMER_ID = 
            given()
                .basePath("/auth/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .body()
                                .as(UserDTO.class)
									.id();

        AccountCredentials accountCredentials = 
            new AccountCredentials(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD);

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
			.build();
    }

    @Test
    @Order(100)
    void testFindAllByUserAsCustomerWithUserIdInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", 0)
				.when()
				    .get("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive number.", output.detail());
        assertEquals("/api/v1/user/0/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testFindAllByUserAsCustomerWithOtherUser() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID+1)
				.when()
				    .get("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(100)
    void testFindAllByUserAsCustomerWithSameUserAndNoItem() {
        List<ShoppingCartItemResponseDTO> output =
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
				.when()
				    .get("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .jsonPath()
                                    .getList("", ShoppingCartItemResponseDTO.class);
        
        assertEquals(0, output.size());
    }

    @Test
    @Order(110)
    void testAddItemToCartAsCustomerWithParamUserIdInvalid() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 5);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", 0)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive number.", output.detail());
        assertEquals("/api/v1/user/0/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testAddItemToCartAsCustomerWithoutBody() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testAddItemToCartAsCustomerWithBodyFieldsInvalid() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(-2L, null);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart", output.instance());
		assertEquals(2, output.errors().size());
        assertEquals("The id of inventory item must be a positive number.", output.errors().get("idInventoryItem"));
        assertEquals("The quantity must be positive number.", output.errors().get("quantity"));
    }

    @Test
    @Order(110)
    void testAddItemToCartAsCustomerWithOtherUser() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 5);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID+1)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testAddItemToCartAsCustomerWithSameUserAndItemNotFound() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(9999L, 5);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not found with the given ID.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testAddItemToCartAsCustomerWithSameUserAndSuccess() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 5);

        ShoppingCartItemResponseDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ShoppingCartItemResponseDTO.class);

        assertEquals(2L, output.idInventoryItem());
        assertEquals("Founders of Teotihuacan", output.name());
        assertEquals("HLXKCCTW7", output.version());
        assertEquals("Used", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("100")));
		assertEquals(5, output.quantity());
    }

    @Test
    @Order(111)
    void testAddItemToCartAsCustomerWithSameUserAndItemAlreadyAdd() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 5);

         APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.CONFLICT.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Conflict", output.title());
        assertEquals(HttpStatus.CONFLICT.value(), output.status());
        assertEquals("The inventory item was already add to the cart.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/shopping-cart", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(111)
    void testFindAllByUserAsCustomerWithSameUserAndItemAdd() {
        List<ShoppingCartItemResponseDTO> output =
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
				.when()
				    .get("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .jsonPath()
                                    .getList("", ShoppingCartItemResponseDTO.class);
        
        assertEquals(1, output.size());

        ShoppingCartItemResponseDTO outputPosition0 = output.get(0);
        assertEquals(2L, outputPosition0.idInventoryItem());
        assertEquals("Founders of Teotihuacan", outputPosition0.name());
        assertEquals("HLXKCCTW7", outputPosition0.version());
        assertEquals("Used", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("100")));
		assertEquals(5, outputPosition0.quantity());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithoutBody() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 2)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithBodyFieldsInvalid() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(null, -2);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 2)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart/2", output.instance());
		assertEquals(2, output.errors().size());
        assertEquals("The id of inventory item must be a positive number.", output.errors().get("idInventoryItem"));
        assertEquals("The quantity must be positive number.", output.errors().get("quantity"));
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithParamUserIdInvalid() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 1);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", 0)
                    .pathParam("inventory-item-id", 2)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive number.", output.detail());
        assertEquals("/api/v1/user/0/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithParamInventoryItemIdInvalid() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 1);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", -2)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The inventory-item-id must be a positive number.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart/-2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithMismatchParamInventoryIdAndDTOId() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 1);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 3)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the inventory-item-id parameter.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart/3", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithOtherUser() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 1);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID+1)
                    .pathParam("inventory-item-id", 2)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithSameUserAndItemNotAddToCart() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(1L, 1);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 1)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not added to the cart.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart/1", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateItemInCartAsCustomerWithSameUserAndSuccess() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(2L, 1);

        ShoppingCartItemResponseDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 2)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ShoppingCartItemResponseDTO.class);

        assertEquals(2L, output.idInventoryItem());
        assertEquals("Founders of Teotihuacan", output.name());
        assertEquals("HLXKCCTW7", output.version());
        assertEquals("Used", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("100")));
		assertEquals(1, output.quantity());
    }

    @Test
    @Order(130)
    void testRemoveItemFromCartAsCustomerWithParamUserIdInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", 0)
                    .pathParam("inventory-item-id", 2)
				.when()
				    .delete("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive number.", output.detail());
        assertEquals("/api/v1/user/0/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testRemoveItemFromCartAsCustomerWithParamInventoryIdInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 0)
				.when()
				    .delete("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The inventory-item-id must be a positive number.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/shopping-cart/0", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testRemoveItemFromCartAsCustomerWithOtherUser() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID+1)
                    .pathParam("inventory-item-id", 2)
				.when()
				    .delete("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/shopping-cart/2", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testRemoveItemFromCartAsCustomerWithSameUserAndItemNotAdd() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .delete("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not added to the cart.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/shopping-cart/1", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(131)
    void testRemoveItemFromCartAsCustomerWithSameUserAndSuccess(){
        given()
            .spec(specification)
                .pathParam("user-id", CUSTOMER_ID)
                .pathParam("inventory-item-id", 2)
			.when()
				.delete("/{user-id}/shopping-cart/{inventory-item-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(199)
    void authenticationAsAdmin() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(
                TestConfigs.USER_ADMIN_EMAIL, 
                TestConfigs.USER_ADMIN_PASSWORD);

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
			.build();
    }

    @Test
    @Order(200)
    void testFindAllByUserAsAdmin() {
        List<ShoppingCartItemResponseDTO> output =
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
				.when()
				    .get("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .jsonPath()
                                    .getList("", ShoppingCartItemResponseDTO.class);
        
        assertEquals(0, output.size());
    }

    @Test
    @Order(210)
    void testAddItemToCartAsAdmin() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(1L, 2);

        ShoppingCartItemResponseDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .body(requestBody)
				.when()
				    .post("/{user-id}/shopping-cart")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ShoppingCartItemResponseDTO.class);

        assertEquals(1L, output.idInventoryItem());
        assertEquals("Peaky Blinders: Birmingham Domain", output.name());
        assertEquals("CRU6WKLQA", output.version());
        assertEquals("Sealed", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("190.65")));
		assertEquals(2, output.quantity());
    }

    @Test
    @Order(220)
    void testUpdateItemInCartAsAdmin() {
        ShoppingCartItemRequestDTO requestBody 
            = new ShoppingCartItemRequestDTO(1L, 3);

        ShoppingCartItemResponseDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", CUSTOMER_ID)
                    .pathParam("inventory-item-id", 1)
                    .body(requestBody)
				.when()
				    .put("/{user-id}/shopping-cart/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ShoppingCartItemResponseDTO.class);

        assertEquals(1L, output.idInventoryItem());
        assertEquals("Peaky Blinders: Birmingham Domain", output.name());
        assertEquals("CRU6WKLQA", output.version());
        assertEquals("Sealed", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("190.65")));
		assertEquals(3, output.quantity());
    }

    @Test
    @Order(230)
    void testRemoveItemFromCartAsAdmin(){
        given()
            .spec(specification)
                .pathParam("user-id", CUSTOMER_ID)
                .pathParam("inventory-item-id", 1)
			.when()
				.delete("/{user-id}/shopping-cart/{inventory-item-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
	@Order(999)
	void removeUserTest() {
		given()
			.spec(specification)
				.pathParam("id", CUSTOMER_ID)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

}
