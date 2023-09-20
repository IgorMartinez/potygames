package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
import br.com.igormartinez.potygames.data.request.OrderAddressRequestDTO;
import br.com.igormartinez.potygames.data.request.OrderItemResquestDTO;
import br.com.igormartinez.potygames.data.request.OrderRequestDTO;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.data.response.OrderDetailResponseDTO;
import br.com.igormartinez.potygames.data.response.OrderResponseDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.potygames.mocks.OrderMocker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Test methodology:
 * When not authenticated, the user should receive a status 403 - Forbidden.
 * When authenticated with a newly created user:
 * - Check if the findAll route is without orders (and no orders from other users 
 * were displayed)
 * - Verify all possibilities of the createOrder route (perform the inclusion of 
 * two orders for later findAll testing)
 * - Verify all possibilities of the findById route
 * - Verify all possibilities of the cancelOrder route (cancel both created orders)
 * - Check the findAll route to ensure both created orders are correct and canceled 
 * (and no orders from other users were displayed).
 * Finally, remove the user created for testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class OrderControllerTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;
    private static String BASE_PATH = "/api/v1/order";

    private static Long CUSTOMER_ID; // signupAndAuthenticationAsAuthenticated
    private static String CUSTOMER_EMAIL = "ordercontroller@customer.test";
	private static String CUSTOMER_PASSWORD = "securedpassword";
    private static String CUSTOMER_ACCESS_TOKEN;
    private static Long ORDER_ID; // testCreateAsAuthenticatedWithSuccess()

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
    void testFindAllAsUnauthenticated() {
        APIErrorResponse output = 
            given()
                .spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", 1)
				.when()
				    .get("/{order-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals(BASE_PATH+"/1", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testCreateOrderAsUnauthenticated() {
        APIErrorResponse output = 
            given()
                .spec(specification)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testCancelOrderAsUnauthenticated() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", 1)
				.when()
				    .put("/{order-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals(BASE_PATH+"/1", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(99)
    void signupAndAuthentication() {
		UserRegistrationDTO user = 
            new UserRegistrationDTO(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD, 
                "Signup Test", 
                LocalDate.of(1996,9,15), 
                "015.009.023-00",
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
                            .as(UserDTO.class)
                                .id();

        AccountCredentials accountCredentials = 
            new AccountCredentials(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD);

        CUSTOMER_ACCESS_TOKEN = 
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
			.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + CUSTOMER_ACCESS_TOKEN)
			.setBasePath("/api/v1/order")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.build();
    }

    @Test
    @Order(100)
    void testFindAllAsAuthenticatedWithoutOrders() {
        List<OrderDetailResponseDTO> output =
            given()
                .spec(specification)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .jsonPath()
                                .getList(".", OrderDetailResponseDTO.class); 
        
        assertEquals(0, output.size());
    }

    @Test
    @Order(110)
    void testCreateOrderAsAuthenticatedWithoutBody() {
        APIErrorResponse output = 
            given()
                .spec(specification)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testCreateOrderAsAuthenticatedWithFieldsNull() {
        OrderRequestDTO requestDTO = new OrderRequestDTO(
            null, null, null
        );

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertEquals(3, output.errors().size());
        assertEquals("The items of order must be provided.", output.errors().get("items"));
        assertEquals("The billing address of order must be provided.", output.errors().get("billingAddress"));
        assertEquals("The delivery address of order must be provided.", output.errors().get("deliveryAddress"));
    }

    @Test
    @Order(110)
    void testCreateOrderAsAuthenticatedWithItemsDuplicated() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(1L, 2),
            new OrderItemResquestDTO(1L, 3)
        );
        OrderRequestDTO requestDTO = new OrderRequestDTO(
            items, 
            OrderMocker.mockAddressRequestDTO(1), 
            OrderMocker.mockAddressRequestDTO(1)
        );

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertEquals(1, output.errors().size());
        assertEquals("The list of items cannot have duplicated elements.", output.errors().get("items"));
    }

    @Test
    @Order(110)
    void testCreateOrderAsAuthenticatedWithFieldsInvalid() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(4L, null),
            new OrderItemResquestDTO(-5L, -2)
        );
        OrderAddressRequestDTO billingAddress = new OrderAddressRequestDTO(
            "Street 1", "10", null, "", 
            "", null, "Brasil", "00000-001");
        OrderAddressRequestDTO deliveryAddress = new OrderAddressRequestDTO(
            "", null, "Home", "Neighborhood 2", 
            "City", "São Paulo", "", null);
        OrderRequestDTO requestDTO = new OrderRequestDTO(
            items, 
            billingAddress, 
            deliveryAddress
        );

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertEquals(10, output.errors().size());
        assertEquals("The quantity must be positive number.", output.errors().get("items[0].quantity"));
        assertEquals("The id of inventory item must be a positive number.", output.errors().get("items[1].idInventoryItem"));
        assertEquals("The quantity must be positive number.", output.errors().get("items[1].quantity"));
        assertEquals("The neighborhood must be not blank.", output.errors().get("billingAddress.neighborhood"));
        assertEquals("The city must be not blank.", output.errors().get("billingAddress.city"));
        assertEquals("The state must be not blank.", output.errors().get("billingAddress.state"));
        assertEquals("The street must be not blank.", output.errors().get("deliveryAddress.street"));
        assertEquals("The number must be not blank.", output.errors().get("deliveryAddress.number"));
        assertEquals("The country must be not blank.", output.errors().get("deliveryAddress.country"));
        assertEquals("The zip code must be not blank.", output.errors().get("deliveryAddress.zipCode"));
    }

    @Test
    @Order(110)
    void testCreateOrderAsAuthenticatedWithItemNotFound() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(4L, 1),
            new OrderItemResquestDTO(5000L, 2)
        );
        OrderAddressRequestDTO address = OrderMocker.mockAddressRequestDTO(1);
        OrderRequestDTO requestDTO = new OrderRequestDTO(items, address, address);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not found with the given ID.", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testCreateOrderAsAuthenticatedWithInsufficientQuantity() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(4L, 1),
            new OrderItemResquestDTO(5L, 2000)
        );
        OrderAddressRequestDTO address = OrderMocker.mockAddressRequestDTO(1);
        OrderRequestDTO requestDTO = new OrderRequestDTO(items, address, address);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.CONFLICT.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Conflict", output.title());
        assertEquals(HttpStatus.CONFLICT.value(), output.status());
        assertEquals("The order exceeded the quantity in inventory.", output.detail());
        assertEquals(BASE_PATH, output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(111)
    void testCreateOrderAsAuthenticatedWithSuccess() {
        List<OrderItemResquestDTO> items = List.of(
            new OrderItemResquestDTO(4L, 1),
            new OrderItemResquestDTO(5L, 2)
        );
        OrderAddressRequestDTO billingAddress = new OrderAddressRequestDTO(
            "Street Saint Paul", "7", "Home", "Saint Thomas Aquinas", 
            "Saint George", "Saint Peter", "Vatican", "00000-000");
        OrderAddressRequestDTO deliveryAddress = new OrderAddressRequestDTO(
            "Street Saint Teresa of Ávila", "8", "Store", "Saint Joan of Arc", 
            "Saint Therese of Lisieux", "Saint Clare of Assisi", "Italy", "00000-001");
        OrderRequestDTO requestDTO = new OrderRequestDTO(items, billingAddress, deliveryAddress);

        OrderResponseDTO output = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(OrderResponseDTO.class);

        assertTrue(output.id() > 0);
        assertEquals("CONFIRMED", output.status());

        ORDER_ID = output.id();
    }

    @Test
    @Order(112)
    void testCreateOrderAsAuthenticatedWithSuccessSecondTime() {
        // Insert one more to verify find all with 2 elements
        OrderRequestDTO requestDTO = new OrderRequestDTO(
            List.of(new OrderItemResquestDTO(1L, 1)), 
            OrderMocker.mockAddressRequestDTO(1), 
            OrderMocker.mockAddressRequestDTO(1)
        );
        OrderResponseDTO outputNewOrder = 
            given()
                .spec(specification)
                    .body(requestDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(OrderResponseDTO.class);
        assertTrue(outputNewOrder.id() > 0);
        assertEquals("CONFIRMED", outputNewOrder.status());
    }

    @Test
    @Order(120)
    void testFindByIdAsAuthenticatedWithParamInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", -10)
				.when()
				    .get("/{order-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The order-id must be a positive number.", output.detail());
        assertEquals(BASE_PATH+"/-10", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testFindByIdAsAuthenticatedWithOrderNotFound() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", 9999)
				.when()
				    .get("/{order-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The order was not found with the given ID.", output.detail());
        assertEquals(BASE_PATH+"/9999", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testFindByIdAsAuthenticatedWithOtherUser() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", 1)
				.when()
				    .get("/{order-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals(BASE_PATH+"/1", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testFindByIdAsAuthenticatedWithSameUser() {
        // see: testCreateAsAuthenticatedWithSuccess()

        OrderDetailResponseDTO output =
            given()
                .spec(specification)
                    .pathParam("order-id", ORDER_ID)
				.when()
				    .get("/{order-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(OrderDetailResponseDTO.class);

        assertEquals(ORDER_ID, output.id());
        assertEquals("CONFIRMED", output.status());
        assertEquals(0, output.totalPrice().compareTo(new BigDecimal("89.97")));

        assertEquals(2, output.items().size());
        assertEquals(4, output.items().get(0).id());
        assertEquals("Orange Cassidy - I Do Not Have A Catchphrase", output.items().get(0).name());
        assertEquals("Orange", output.items().get(0).version());
        assertEquals("New", output.items().get(0).condition());
        assertEquals(0, output.items().get(0).unitPrice().compareTo(new BigDecimal("29.99")));
        assertEquals(1, output.items().get(0).quantity());
        assertEquals(5, output.items().get(1).id());
        assertEquals("Hikaru Shida - Shining Samurai Anime", output.items().get(1).name());
        assertEquals("Black", output.items().get(1).version());
        assertEquals("New", output.items().get(1).condition());
        assertEquals(0, output.items().get(1).unitPrice().compareTo(new BigDecimal("29.99")));
        assertEquals(2, output.items().get(1).quantity());

        assertEquals("Street Saint Paul", output.billingAddress().street());
        assertEquals("7", output.billingAddress().number());
        assertEquals("Home", output.billingAddress().complement());
        assertEquals("Saint Thomas Aquinas", output.billingAddress().neighborhood());
        assertEquals("Saint George", output.billingAddress().city());
        assertEquals("Saint Peter", output.billingAddress().state());
        assertEquals("Vatican", output.billingAddress().country());
        assertEquals("00000-000", output.billingAddress().zipCode());
    
        assertEquals("Street Saint Teresa of Ávila", output.deliveryAddress().street());
        assertEquals("8", output.deliveryAddress().number());
        assertEquals("Store", output.deliveryAddress().complement());
        assertEquals("Saint Joan of Arc", output.deliveryAddress().neighborhood());
        assertEquals("Saint Therese of Lisieux", output.deliveryAddress().city());
        assertEquals("Saint Clare of Assisi", output.deliveryAddress().state());
        assertEquals("Italy", output.deliveryAddress().country());
        assertEquals("00000-001", output.deliveryAddress().zipCode());
    }

    @Test
    @Order(130)
    void testCancelOrderAsAuthenticatedWithParamInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", -9)
				.when()
				    .put("/{order-id}/cancel")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The order-id must be a positive number.", output.detail());
        assertEquals(BASE_PATH+"/-9/cancel", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testCancelOrderAsAuthenticatedWithOrderNotFound() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", 9999)
				.when()
				    .put("/{order-id}/cancel")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The order was not found with the given ID.", output.detail());
        assertEquals(BASE_PATH+"/9999/cancel", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testCancelOrderAsAuthenticatedWithOtherUser() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", 1)
				.when()
				    .put("/{order-id}/cancel")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals(BASE_PATH+"/1/cancel", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testCancelOrderAsAuthenticatedWithSuccess() {
        // see: testCreateAsAuthenticatedWithSuccess()

        OrderResponseDTO output =
            given()
                .spec(specification)
                    .pathParam("order-id", ORDER_ID)
				.when()
				    .put("/{order-id}/cancel")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(OrderResponseDTO.class);
        
        assertEquals(ORDER_ID, output.id());
        assertEquals("CANCELED", output.status());

        // Verify if the quantity return to inventory
        InventoryItemDTO inventoryItemDTO =
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 4)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        assertEquals(4L, inventoryItemDTO.id());
        assertEquals(4, inventoryItemDTO.quantity());

        inventoryItemDTO =
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 5)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        assertEquals(5L, inventoryItemDTO.id());
        assertEquals(3, inventoryItemDTO.quantity());
    }

    @Test
    @Order(131)
    void testCancelOrderAsAuthenticatedWithSuccessSecondTime() {
        OrderResponseDTO output =
            given()
                .spec(specification)
                    .pathParam("order-id", ORDER_ID+1)
				.when()
				    .put("/{order-id}/cancel")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(OrderResponseDTO.class);
        
        assertEquals(ORDER_ID+1, output.id());
        assertEquals("CANCELED", output.status());
    }

    @Test
    @Order(131)
    void testCancelOrderAsAuthenticatedWithOrderAlreadyCancel() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("order-id", ORDER_ID)
				.when()
				    .put("/{order-id}/cancel")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The order is already cancelled.", output.detail());
        assertEquals(BASE_PATH+"/"+ORDER_ID+"/cancel", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(140)
    void testFindAllAsAuthenticatedWithOrders() {
        // Test findAll
        List<OrderDetailResponseDTO> output =
            given()
                .spec(specification)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .jsonPath()
                                .getList(".", OrderDetailResponseDTO.class); 
        assertEquals(2, output.size());

        // Order position 0
        OrderDetailResponseDTO outputPosition0 = output.get(0);
        assertEquals(ORDER_ID, outputPosition0.id());
        assertEquals("CANCELED", outputPosition0.status());
        assertEquals(0, outputPosition0.totalPrice().compareTo(new BigDecimal("89.97")));

        assertEquals(2, outputPosition0.items().size());
        assertEquals(4, outputPosition0.items().get(0).id());
        assertEquals("Orange Cassidy - I Do Not Have A Catchphrase", outputPosition0.items().get(0).name());
        assertEquals("Orange", outputPosition0.items().get(0).version());
        assertEquals("New", outputPosition0.items().get(0).condition());
        assertEquals(0, outputPosition0.items().get(0).unitPrice().compareTo(new BigDecimal("29.99")));
        assertEquals(1, outputPosition0.items().get(0).quantity());
        assertEquals(5, outputPosition0.items().get(1).id());
        assertEquals("Hikaru Shida - Shining Samurai Anime", outputPosition0.items().get(1).name());
        assertEquals("Black", outputPosition0.items().get(1).version());
        assertEquals("New", outputPosition0.items().get(1).condition());
        assertEquals(0, outputPosition0.items().get(1).unitPrice().compareTo(new BigDecimal("29.99")));
        assertEquals(2, outputPosition0.items().get(1).quantity());

        assertEquals("Street Saint Paul", outputPosition0.billingAddress().street());
        assertEquals("7", outputPosition0.billingAddress().number());
        assertEquals("Home", outputPosition0.billingAddress().complement());
        assertEquals("Saint Thomas Aquinas", outputPosition0.billingAddress().neighborhood());
        assertEquals("Saint George", outputPosition0.billingAddress().city());
        assertEquals("Saint Peter", outputPosition0.billingAddress().state());
        assertEquals("Vatican", outputPosition0.billingAddress().country());
        assertEquals("00000-000", outputPosition0.billingAddress().zipCode());
    
        assertEquals("Street Saint Teresa of Ávila", outputPosition0.deliveryAddress().street());
        assertEquals("8", outputPosition0.deliveryAddress().number());
        assertEquals("Store", outputPosition0.deliveryAddress().complement());
        assertEquals("Saint Joan of Arc", outputPosition0.deliveryAddress().neighborhood());
        assertEquals("Saint Therese of Lisieux", outputPosition0.deliveryAddress().city());
        assertEquals("Saint Clare of Assisi", outputPosition0.deliveryAddress().state());
        assertEquals("Italy", outputPosition0.deliveryAddress().country());
        assertEquals("00000-001", outputPosition0.deliveryAddress().zipCode());

        // Order position 1
        OrderDetailResponseDTO outputPosition1 = output.get(1);
        assertEquals(ORDER_ID+1, outputPosition1.id());
        assertEquals("CANCELED", outputPosition1.status());
        assertEquals(0, outputPosition1.totalPrice().compareTo(new BigDecimal("190.65")));

        assertEquals(1, outputPosition1.items().size());
        assertEquals(1, outputPosition1.items().get(0).id());
        assertEquals("Peaky Blinders: Birmingham Domain", outputPosition1.items().get(0).name());
        assertEquals("CRU6WKLQA", outputPosition1.items().get(0).version());
        assertEquals("Sealed", outputPosition1.items().get(0).condition());
        assertEquals(0, outputPosition1.items().get(0).unitPrice().compareTo(new BigDecimal("190.65")));
        assertEquals(1, outputPosition1.items().get(0).quantity());

        assertEquals("Street 1", outputPosition1.billingAddress().street());
        assertEquals("Number 1", outputPosition1.billingAddress().number());
        assertEquals("Complement 1", outputPosition1.billingAddress().complement());
        assertEquals("Neighborhood 1", outputPosition1.billingAddress().neighborhood());
        assertEquals("City 1", outputPosition1.billingAddress().city());
        assertEquals("State 1", outputPosition1.billingAddress().state());
        assertEquals("Country 1", outputPosition1.billingAddress().country());
        assertEquals("00000-001", outputPosition1.billingAddress().zipCode());

        assertEquals("Street 1", outputPosition1.deliveryAddress().street());
        assertEquals("Number 1", outputPosition1.deliveryAddress().number());
        assertEquals("Complement 1", outputPosition1.deliveryAddress().complement());
        assertEquals("Neighborhood 1", outputPosition1.deliveryAddress().neighborhood());
        assertEquals("City 1", outputPosition1.deliveryAddress().city());
        assertEquals("State 1", outputPosition1.deliveryAddress().state());
        assertEquals("Country 1", outputPosition1.deliveryAddress().country());
        assertEquals("00000-001", outputPosition1.deliveryAddress().zipCode());
    }

    @Test
	@Order(1000)
	void removeUserTest() {
		given()
            .basePath("/api/v1/user")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + CUSTOMER_ACCESS_TOKEN)
				.pathParam("id", CUSTOMER_ID)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}
}
