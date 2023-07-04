package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
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
public class ProductControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";
    private static String CUSTOMER_EMAIL = "fragge1@blinklist.com";
    private static String CUSTOMER_PASSWORD = "ZkIfFOo";

    private static Long PRODUCT_ID;

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedWithoutQueryParams() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", ProductDTO.class);

        assertNotNull(output);
        assertEquals(6, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Peaky Blinders: Birmingham Domain", outputPosition0.name());
        assertTrue(outputPosition0.description().startsWith("Completely based on the famous TV series"));

        ProductDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(3L, outputPosition5.idProductType());
        assertEquals("Battle of Legends: Armagedon", outputPosition5.name());
        assertTrue(outputPosition5.description().startsWith("Booster Box containing 24"));
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedPage1Size3DirectionASC() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
                    .queryParam("page", 1)
                    .queryParam("size", 3)
                    .queryParam("direction", "asc")
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", ProductDTO.class);

        assertNotNull(output);
        assertEquals(3, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(4, outputPosition0.id());
        assertEquals(3L, outputPosition0.idProductType());
        assertEquals("Cyberstorm Access", outputPosition0.name());
        assertTrue(outputPosition0.description().startsWith("Booster Box containing"));

        ProductDTO outputPosition2 = output.get(2);
        assertEquals(6, outputPosition2.id());
        assertEquals(3L, outputPosition2.idProductType());
        assertEquals("Battle of Legends: Armagedon", outputPosition2.name());
        assertTrue(outputPosition2.description().startsWith("Booster Box containing"));
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedPage0Size12DirectionASC() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
                    .queryParam("page", 0)
                    .queryParam("size", 12)
                    .queryParam("direction", "asc")
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", ProductDTO.class);

        assertNotNull(output);
        assertEquals(6, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Peaky Blinders: Birmingham Domain", outputPosition0.name());
        assertTrue(outputPosition0.description().startsWith("Completely based on the famous TV series"));

        ProductDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(3L, outputPosition5.idProductType());
        assertEquals("Battle of Legends: Armagedon", outputPosition5.name());
        assertTrue(outputPosition5.description().startsWith("Booster Box containing 24"));
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedPage0Size10DirectionDESC() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .queryParam("direction", "desc")
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", ProductDTO.class);

        assertNotNull(output);
        assertEquals(6, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(6, outputPosition0.id());
        assertEquals(3L, outputPosition0.idProductType());
        assertEquals("Battle of Legends: Armagedon", outputPosition0.name());
        assertTrue(outputPosition0.description().startsWith("Booster Box containing"));

        ProductDTO outputPosition5 = output.get(5);
        assertEquals(1L, outputPosition5.id());
        assertEquals(1L, outputPosition5.idProductType());
        assertEquals("Peaky Blinders: Birmingham Domain", outputPosition5.name());
        assertTrue(outputPosition5.description().startsWith("Completely based on the famous TV series"));

    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        ProductDTO output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-id", 3)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(3L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Structure Deck: Legend of the Crystal Beasts", output.name());
        assertTrue(output.description().startsWith("Each Structure Deck: Legend"));
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamIdInvalid() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-id", 0)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/0", output.getInstance());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithProductNotFound() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-id", 12546)
				.when()
				    .get("/{product-id}")
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
        assertEquals("/api/v1/product/12546", output.getInstance());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null);

        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(productDTO)
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
        assertEquals("/api/v1/product", output.getInstance());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        ProductDTO productDTO = new ProductDTO(1L, 1L, 
            "Product name 1", null);

        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-id", 1)
                    .body(productDTO)
				.when()
				    .put("/{product-id}")
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
        assertEquals("/api/v1/product/1", output.getInstance());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-id", 1)
				.when()
				    .delete("/{product-id}")
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
        assertEquals("/api/v1/product/1", output.getInstance());
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
			.setBasePath("/api/v1/product")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(101)
    void testFindAllAsAdmin() {
        List<ProductDTO> output = 
            given()
				.spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", ProductDTO.class);

        assertNotNull(output);
        assertEquals(6, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Peaky Blinders: Birmingham Domain", outputPosition0.name());
        assertTrue(outputPosition0.description().startsWith("Completely based on the famous TV series"));

        ProductDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(3L, outputPosition5.idProductType());
        assertEquals("Battle of Legends: Armagedon", outputPosition5.name());
        assertTrue(outputPosition5.description().startsWith("Booster Box containing 24"));
    }

    @Test
    @Order(101)
    void testFindByIdAsAdmin() {
        ProductDTO output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 3)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(3L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Structure Deck: Legend of the Crystal Beasts", output.name());
        assertTrue(output.description().startsWith("Each Structure Deck: Legend"));
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null);

        ProductDTO output =
			given()
				.spec(specification)
					.body(productDTO)
				.when()
					.post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertTrue(output.id() > 0);
        assertEquals(1L, output.idProductType());
        assertEquals("Product name 1", output.name());
        assertNull(output.description());

        PRODUCT_ID = output.id();
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithBadProductRequest() {
        ProductDTO productDTO = new ProductDTO(null, null, 
            null, null);

        ExceptionResponse output =
			given()
				.spec(specification)
					.body(productDTO)
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
        assertEquals("The product type ID must not be null.", output.getDetail());
        assertEquals("/api/v1/product", output.getInstance());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithNotFoundProductType() {
        ProductDTO productDTO = new ProductDTO(null, 15555L, 
            "Product name 1", null);

        ExceptionResponse output =
			given()
				.spec(specification)
					.body(productDTO)
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
        assertEquals("The product type was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdatAsAdmin() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID, 2L, 
            "Product updated name 1", "Description 1");

        ProductDTO output =
			given()
				.spec(specification)
                    .pathParam("product-id", PRODUCT_ID)
					.body(productDTO)
				.when()
					.put("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(PRODUCT_ID, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Product updated name 1", output.name());
        assertEquals("Description 1", output.description());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID, 2L, 
            "Product updated name 1", "Description 1");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", 0)
					.body(productDTO)
				.when()
					.put("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/0", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID, 2L, 
            "Product updated name 1", "Description 1");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", 1024)
					.body(productDTO)
				.when()
					.put("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The ID in the request body must match the value of the product-id parameter.", output.getDetail());
        assertEquals("/api/v1/product/1024", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamDTOInvalid() {
        ProductDTO productDTO = new ProductDTO(1L, null, 
            null, null);

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", 1)
					.body(productDTO)
				.when()
					.put("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product type ID must not be null.", output.getDetail());
        assertEquals("/api/v1/product/1", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductTypeNotFound() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID, 1024L, 
            "Product updated name 1", "Product description 1");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", PRODUCT_ID)
					.body(productDTO)
				.when()
					.put("/{product-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The product type was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product/"+PRODUCT_ID, output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductNotFound() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID+1, 2L, 
            "Product updated name 1", "Product description 1");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", PRODUCT_ID+1)
					.body(productDTO)
				.when()
					.put("/{product-id}")
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
        assertEquals("/api/v1/product/"+(PRODUCT_ID+1), output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithParamIdInvalid() {
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", 0)
				.when()
					.delete("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/0", output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithProductNotFound() {
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", PRODUCT_ID+1)
				.when()
					.delete("/{product-id}")
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
        assertEquals("/api/v1/product/"+(PRODUCT_ID+1), output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdmin() {
		given()
			.spec(specification)
                .pathParam("product-id", PRODUCT_ID)
			.when()
				.delete("/{product-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
					.extract()
						.body()
                            .asString();
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
			.setBasePath("/api/v1/product")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(201)
    void testFindAllAsCustomer() {
        List<ProductDTO> output = 
            given()
				.spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", ProductDTO.class);

        assertNotNull(output);
        assertEquals(6, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Peaky Blinders: Birmingham Domain", outputPosition0.name());
        assertTrue(outputPosition0.description().startsWith("Completely based on the famous TV series"));

        ProductDTO outputPosition5 = output.get(5);
        assertEquals(6L, outputPosition5.id());
        assertEquals(3L, outputPosition5.idProductType());
        assertEquals("Battle of Legends: Armagedon", outputPosition5.name());
        assertTrue(outputPosition5.description().startsWith("Booster Box containing 24"));
    }

    @Test
    @Order(201)
    void testFindByIdAsCustomer() {
        ProductDTO output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 3)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(3L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Structure Deck: Legend of the Crystal Beasts", output.name());
        assertTrue(output.description().startsWith("Each Structure Deck: Legend"));
    }

    @Test
    @Order(210)
    void testCreateAsCustomer() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null);

        ExceptionResponse output = 
            given()
				.spec(specification)
                    .body(productDTO)
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
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/v1/product", output.getInstance());
    }

    @Test
    @Order(210)
    void testUpdateAsCustomer() {
        ProductDTO productDTO = new ProductDTO(1L, 1L, 
            "Product name 1", null);

        ExceptionResponse output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 1)
                    .body(productDTO)
				.when()
				    .put("/{product-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/v1/product/1", output.getInstance());
    }

    @Test
    @Order(210)
    void testDeleteAsCustomer() {
        ExceptionResponse output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 1)
				.when()
				    .delete("/{product-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/v1/product/1", output.getInstance());
    }

}
