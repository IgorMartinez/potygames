package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import br.com.igormartinez.potygames.data.dto.v1.InventoryItemDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class InventoryItemControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";
    private static String CUSTOMER_EMAIL = "fragge1@blinklist.com";
    private static String CUSTOMER_PASSWORD = "ZkIfFOo";

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

        
        assertEquals(2, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("CRU6WKLQA", outputPosition0.version());
        assertEquals("Sealed", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, outputPosition0.quantity());

        InventoryItemDTO outputPosition1 = output.get(1);
        assertEquals(2L, outputPosition1.id());
        assertEquals(2L, outputPosition1.product());
        assertEquals("HLXKCCTW7", outputPosition1.version());
        assertEquals("Used", outputPosition1.condition());
        assertEquals(0, outputPosition1.price().compareTo(new BigDecimal("100")));
        assertEquals(1, outputPosition1.quantity());
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
        ExceptionResponse output =
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
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The inventory-item-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/inventory/0", output.getInstance());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithItemNotFound() {
        ExceptionResponse output =
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
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The inventory item was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/inventory/45425", output.getInstance());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            null, null, 
            null, null, null, null);

        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/inventory", output.getInstance());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            null, null, 
            null, null, null, null);

        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/inventory/1", output.getInstance());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/inventory/1", output.getInstance());
    }

    @Test
    @Order(100)
    void authenticationAsAdmin() {
        AccountCredentials accountCredentials = new AccountCredentials(ADMIN_EMAIL, ADMIN_PASSWORD);

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
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
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

        assertEquals(2, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("CRU6WKLQA", outputPosition0.version());
        assertEquals("Sealed", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, outputPosition0.quantity());

        InventoryItemDTO outputPosition1 = output.get(1);
        assertEquals(2L, outputPosition1.id());
        assertEquals(2L, outputPosition1.product());
        assertEquals("HLXKCCTW7", outputPosition1.version());
        assertEquals("Used", outputPosition1.condition());
        assertEquals(0, outputPosition1.price().compareTo(new BigDecimal("100")));
        assertEquals(1, outputPosition1.quantity());
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
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            null, 1L, 
            "BRA2002R9", "Used", new BigDecimal("180.55"), 2);

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
        ExceptionResponse output =
            given()
                .spec(specification)
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Failed to read request", output.getDetail());
        assertEquals("/api/v1/inventory", output.getInstance());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithBadItemRequest() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            null, 1L, 
            "SDCB-PT004", "M", new BigDecimal("-0.50"), 3);
        
        ExceptionResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The price must be null, zero or positive.", output.getDetail());
        assertEquals("/api/v1/inventory", output.getInstance());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithItemNotFound() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            null, 555555L, 
            "SDCB-PT004", "M", new BigDecimal("0.50"), 3);
        
        ExceptionResponse output =
            given()
                .spec(specification)
                    .body(itemDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The product was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/inventory", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdmin() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
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
    void testUpdateAsAdminWithParamIdInvalid() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            INVENTORY_ITEM_ID, 2L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        ExceptionResponse output =
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
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The inventory-item-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/inventory/0", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            INVENTORY_ITEM_ID, 2L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        ExceptionResponse output =
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
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The ID in the request body must match the value of the inventory-item-id parameter.", output.getDetail());
        assertEquals("/api/v1/inventory/"+(INVENTORY_ITEM_ID+1), output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithBadItemRequest() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            INVENTORY_ITEM_ID, null,  
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        ExceptionResponse output =
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
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("A product must be provided.", output.getDetail());
        assertEquals("/api/v1/inventory/"+INVENTORY_ITEM_ID, output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithItemNotFound() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            INVENTORY_ITEM_ID, 55555L, 
            "SDCB-PT005", "NM", new BigDecimal("0.43"), 4);
        
        ExceptionResponse output =
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
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The product was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/inventory/"+INVENTORY_ITEM_ID, output.getInstance());
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
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("inventory-item-id", 0)
				.when()
					.delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The inventory-item-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/inventory/0", output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithProductNotFound() {
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("inventory-item-id", INVENTORY_ITEM_ID+1)
				.when()
					.delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The inventory item was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/inventory/"+(INVENTORY_ITEM_ID+1), output.getInstance());
    }

    @Test
    @Order(200)
    void authenticationAsCustomer() {
        AccountCredentials accountCredentials = new AccountCredentials(CUSTOMER_EMAIL, CUSTOMER_PASSWORD);

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
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
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

        
        assertEquals(2, output.size());

        InventoryItemDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.product());
        assertEquals("CRU6WKLQA", outputPosition0.version());
        assertEquals("Sealed", outputPosition0.condition());
        assertEquals(0, outputPosition0.price().compareTo(new BigDecimal("190.65")));
        assertEquals(3, outputPosition0.quantity());

        InventoryItemDTO outputPosition1 = output.get(1);
        assertEquals(2L, outputPosition1.id());
        assertEquals(2L, outputPosition1.product());
        assertEquals("HLXKCCTW7", outputPosition1.version());
        assertEquals("Used", outputPosition1.condition());
        assertEquals(0, outputPosition1.price().compareTo(new BigDecimal("100")));
        assertEquals(1, outputPosition1.quantity());
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
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            null, null,  
            null, null, null, null);

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .body(itemDTO)
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
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/inventory", output.getInstance());
    }

    @Test
    @Order(201)
    void testUpdateAsCustomer() {
        InventoryItemDTO itemDTO = new InventoryItemDTO(
            1L, null,  
            null, null, null, null);

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", 1)
                    .body(itemDTO)
				.when()
				    .put("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/inventory/1", output.getInstance());
    }

    @Test
    @Order(201)
    void testDeleteAsCustomer() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .pathParam("inventory-item-id", 1)
				.when()
				    .delete("/{inventory-item-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/inventory/1", output.getInstance());
    }

}
