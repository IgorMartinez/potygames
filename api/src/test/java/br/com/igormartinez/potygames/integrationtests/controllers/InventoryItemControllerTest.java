package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.InventoryItemCreateDTO;
import br.com.igormartinez.potygames.data.request.InventoryItemUpdateDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class InventoryItemControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static Long INVENTORY_ITEM_ID;
    
    @Test
    @Order(0)
    void testFindAllAsUnauthenticated() {
        List<InventoryItemDTO> output = 
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", InventoryItemDTO.class);

        
        assertEquals(6, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("CRU6WKLQA", outputPosition0.version());
        assertEquals("Sealed", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, outputPosition0.quantity());

        InventoryItemDTO outputPosition2 = output.get(2);
        assertEquals(3L, outputPosition2.id());
        assertEquals(7L, outputPosition2.product());
        assertEquals("White", outputPosition2.version());
        assertEquals("New", outputPosition2.condition());
        assertEquals(0, outputPosition2.price().compareTo(new BigDecimal("29.99")));
        assertEquals(1, outputPosition2.quantity());

        InventoryItemDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(9L, outputPosition5.product());
        assertEquals("Black", outputPosition5.version());
        assertEquals("New", outputPosition5.condition());
        assertEquals(0, outputPosition5.price().compareTo(new BigDecimal("29.99")));
        assertEquals(2, outputPosition5.quantity());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        InventoryItemDTO output =
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        
        assertEquals(1L, output.id());
        assertEquals(1L, output.product());
        assertEquals("CRU6WKLQA", output.version());
        assertEquals("Sealed", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, output.quantity());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamInvalid() {
        APIErrorResponse output =
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 0)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The inventory-item-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/inventory/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithItemNotFound() {
        APIErrorResponse output =
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 45425)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not found with the given ID.", output.detail());
        assertEquals("/api/v1/inventory/45425", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            null, null, null, null, null);

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(itemDTO)
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
        assertEquals("/api/v1/inventory", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            null, null, null, null, null, null);

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 1)
                    .body(itemDTO)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/inventory/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/inventory")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/inventory/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void authenticationAsAdmin() {
        AccountCredentials accountCredentials 
            = new AccountCredentials(TestConfigs.USER_ADMIN_EMAIL, TestConfigs.USER_ADMIN_PASSWORD);

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
			.setBasePath("/api/v1/inventory")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.build();
    }

    @Test
    @Order(101)
    void testFindAllAsAdmin() {
        List<InventoryItemDTO> output = 
            given()
                .spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", InventoryItemDTO.class);

        assertEquals(6, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("CRU6WKLQA", outputPosition0.version());
        assertEquals("Sealed", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, outputPosition0.quantity());

        InventoryItemDTO outputPosition2 = output.get(2);
        assertEquals(3L, outputPosition2.id());
        assertEquals(7L, outputPosition2.product());
        assertEquals("White", outputPosition2.version());
        assertEquals("New", outputPosition2.condition());
        assertEquals(0, outputPosition2.price().compareTo(new BigDecimal("29.99")));
        assertEquals(1, outputPosition2.quantity());

        InventoryItemDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(9L, outputPosition5.product());
        assertEquals("Black", outputPosition5.version());
        assertEquals("New", outputPosition5.condition());
        assertEquals(0, outputPosition5.price().compareTo(new BigDecimal("29.99")));
        assertEquals(2, outputPosition5.quantity());
    }

    @Test
    @Order(101)
    void testFindByIdAsAdmin() {
        InventoryItemDTO output =
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        
        assertEquals(1L, output.id());
        assertEquals(1L, output.product());
        assertEquals("CRU6WKLQA", output.version());
        assertEquals("Sealed", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, output.quantity());
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "BRA2002R9", "Used", new BigDecimal("180.55"), 2);

        InventoryItemDTO output =
            given()
                .spec(specification)
                    .body(itemDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        
        assertTrue(output.id() > 0);
        assertEquals(1L, output.product());
        assertEquals("BRA2002R9", output.version());
        assertEquals("Used", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("180.55")));
        assertEquals(2, output.quantity());

        INVENTORY_ITEM_ID = output.id();
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithoutBody() {
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
        assertEquals("/api/v1/inventory", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithFieldsValidationError() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            -10L, " ", "M", new BigDecimal("-0.50"), null);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
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
        assertEquals("/api/v1/inventory", output.instance());
        assertEquals(4, output.errors().size());
        assertEquals("The id of product must be a positive number.", output.errors().get("product"));
        assertEquals("The version must be not blank.", output.errors().get("version"));
        assertEquals("The price must be null, zero or positive.", output.errors().get("price"));
        assertEquals("The quantity must be provided.", output.errors().get("quantity"));
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithItemNotFound() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            555555L, "BRA2002R9", "Used", new BigDecimal("180.55"), 2);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product was not found with the given ID.", output.detail());
        assertEquals("/api/v1/inventory", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdmin() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            INVENTORY_ITEM_ID, 2L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);

        InventoryItemDTO output =
            given()
                .spec(specification)
                    .body(itemDTO)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        
        assertEquals(INVENTORY_ITEM_ID, output.id());
        assertEquals(2L, output.product());
        assertEquals("SDCB-PT005", output.version());
        assertEquals("NM", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("0.43")));
        assertEquals(4, output.quantity());

        INVENTORY_ITEM_ID = output.id();
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithoutBody() {
        APIErrorResponse output =
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/inventory/"+INVENTORY_ITEM_ID, output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithFieldsValidationError() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            null, 2L, 
            null, "NM", new BigDecimal("0.43"), 4);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/inventory/"+INVENTORY_ITEM_ID, output.instance());
        assertEquals(2, output.errors().size());
        assertEquals("The id must be provided.", output.errors().get("id"));
        assertEquals("The version must be not blank.", output.errors().get("version"));

    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            INVENTORY_ITEM_ID, 2L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
                    .pathParam("inventory-item-id", 0)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The inventory-item-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/inventory/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            INVENTORY_ITEM_ID, 2L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID+1)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the inventory-item-id parameter.", output.detail());
        assertEquals("/api/v1/inventory/"+(INVENTORY_ITEM_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithItemNotFound() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            INVENTORY_ITEM_ID+1, 55555L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID+1)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not found with the given ID.", output.detail());
        assertEquals("/api/v1/inventory/"+(INVENTORY_ITEM_ID+1), output.instance());
        assertNull(output.errors());
    }    

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductNotFound() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            INVENTORY_ITEM_ID, 55555L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        APIErrorResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product was not found with the given ID.", output.detail());
        assertEquals("/api/v1/inventory/"+INVENTORY_ITEM_ID, output.instance());
        assertNull(output.errors());
    }    
    
    @Test
    @Order(130)
    void testDeleteAsAdmin() {
		given()
			.spec(specification)
                .pathParam("inventory-item-id", INVENTORY_ITEM_ID)
			.when()
				.delete("/{inventory-item-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
					.extract()
						.body()
                            .asString();
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithParamIdInvalid() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("inventory-item-id", 0)
				.when()
					.delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The inventory-item-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/inventory/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithProductNotFound() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID+1)
				.when()
					.delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The inventory item was not found with the given ID.", output.detail());
        assertEquals("/api/v1/inventory/"+(INVENTORY_ITEM_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(200)
    void authenticationAsCustomer() {
        AccountCredentials accountCredentials 
            = new AccountCredentials(TestConfigs.USER_CUSTOMER_EMAIL, TestConfigs.USER_CUSTOMER_PASSWORD);

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
			.setBasePath("/api/v1/inventory")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.build();
    }

    @Test
    @Order(201)
    void testFindAllAsCustomer() {
        List<InventoryItemDTO> output = 
            given()
                .spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", InventoryItemDTO.class);

        
        assertEquals(6, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("CRU6WKLQA", outputPosition0.version());
        assertEquals("Sealed", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, outputPosition0.quantity());

        InventoryItemDTO outputPosition2 = output.get(2);
        assertEquals(3L, outputPosition2.id());
        assertEquals(7L, outputPosition2.product());
        assertEquals("White", outputPosition2.version());
        assertEquals("New", outputPosition2.condition());
        assertEquals(0, outputPosition2.price().compareTo(new BigDecimal("29.99")));
        assertEquals(1, outputPosition2.quantity());

        InventoryItemDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(9L, outputPosition5.product());
        assertEquals("Black", outputPosition5.version());
        assertEquals("New", outputPosition5.condition());
        assertEquals(0, outputPosition5.price().compareTo(new BigDecimal("29.99")));
        assertEquals(2, outputPosition5.quantity());
    }

    @Test
    @Order(201)
    void testFindByIdAsCustomer() {
        InventoryItemDTO output =
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .get("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(InventoryItemDTO.class);
        
        assertEquals(1L, output.id());
        assertEquals(1L, output.product());
        assertEquals("CRU6WKLQA", output.version());
        assertEquals("Sealed", output.condition());
        assertEquals(0, output.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, output.quantity());
    }

    @Test
    @Order(201)
    void testCreateAsCustomer() {
        InventoryItemCreateDTO itemDTO = new InventoryItemCreateDTO(
            1L, "BRA2002R9", "Used", new BigDecimal("180.55"), 2);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .body(itemDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/inventory", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(201)
    void testUpdateAsCustomer() {
        InventoryItemUpdateDTO itemDTO = new InventoryItemUpdateDTO(
            INVENTORY_ITEM_ID, 2L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID)
                    .body(itemDTO)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/inventory/"+INVENTORY_ITEM_ID, output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(201)
    void testDeleteAsCustomer() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/inventory/1", output.instance());
        assertNull(output.errors());
    }

}
