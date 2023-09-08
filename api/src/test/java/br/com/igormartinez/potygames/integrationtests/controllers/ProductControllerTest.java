package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.ProductCreateDTO;
import br.com.igormartinez.potygames.data.request.ProductUpdateDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.ProductDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
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

        assertEquals(3L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Structure Deck: Legend of the Crystal Beasts", output.name());
        assertTrue(output.description().startsWith("Each Structure Deck: Legend"));
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamIdInvalid() {
        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The product-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/product/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithProductNotFound() {
        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/12546", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/product", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(1L, 1L, "Lorem Ipsum", "Lorem ipsum dolor sit.");

        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/product/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/product/1", output.instance());
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
			.setBasePath("/api/v1/product")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
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

        assertEquals(3L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Structure Deck: Legend of the Crystal Beasts", output.name());
        assertTrue(output.description().startsWith("Each Structure Deck: Legend"));
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, "Lorem Ipsum", null);

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

        assertTrue(output.id() > 0);
        assertEquals(1L, output.idProductType());
        assertEquals("Lorem Ipsum", output.name());
        assertNull(output.description());

        PRODUCT_ID = output.id();
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
        assertEquals("/api/v1/product", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithFieldsNullOrBlank() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(null, " ", null);

        APIErrorResponse output =
			given()
				.spec(specification)
                    .body(productDTO)
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
        assertEquals("/api/v1/product", output.instance());
        assertEquals(2, output.errors().size());
        assertEquals("The id of product type must be provided.", output.errors().get("idProductType"));
        assertEquals("The name must not be blank.", output.errors().get("name"));
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithProductTypeNotFound() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(55555L, "Lorem Ipsum", null);

        APIErrorResponse output =
			given()
				.spec(specification)
					.body(productDTO)
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
        assertEquals("The product type was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdatAsAdmin() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(PRODUCT_ID, 2L, 
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

        assertEquals(PRODUCT_ID, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Product updated name 1", output.name());
        assertEquals("Description 1", output.description());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(PRODUCT_ID, 2L, 
                "Product updated name 1", "Description 1");

        APIErrorResponse output =
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
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The product-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/product/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        ProductUpdateDTO productDTO 
            = new ProductUpdateDTO(PRODUCT_ID, 2L, 
                "Product updated name 1", "Description 1");

        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", PRODUCT_ID+33)
					.body(productDTO)
				.when()
					.put("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the product-id parameter.", output.detail());
        assertEquals("/api/v1/product/"+(PRODUCT_ID+33), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductTypeNotFound() {
        ProductUpdateDTO productDTO = new ProductUpdateDTO(PRODUCT_ID, 666666L, 
            "Product updated name 1", "Product description 1");

        APIErrorResponse output =
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
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product type was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/"+PRODUCT_ID, output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductNotFound() {
        ProductUpdateDTO productDTO = new ProductUpdateDTO(PRODUCT_ID+1, 2L, 
            "Product updated name 1", "Product description 1");

        APIErrorResponse output =
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
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/"+(PRODUCT_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithParamIdInvalid() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", 0)
				.when()
					.delete("/{product-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The product-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/product/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithProductNotFound() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", PRODUCT_ID+1)
				.when()
					.delete("/{product-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/"+(PRODUCT_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithAssociateInventoryItems() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-id", 1)
				.when()
					.delete("/{product-id}")
				.then()
					.statusCode(HttpStatus.CONFLICT.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Conflict", output.title());
        assertEquals(HttpStatus.CONFLICT.value(), output.status());
        assertEquals("The product cannot be removed because it is associated with inventory items.", output.detail());
        assertEquals("/api/v1/product/1", output.instance());
        assertNull(output.errors());
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

        assertEquals(3L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Structure Deck: Legend of the Crystal Beasts", output.name());
        assertTrue(output.description().startsWith("Each Structure Deck: Legend"));
    }

    @Test
    @Order(210)
    void testCreateAsCustomer() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L, "Product name 1", null);

        APIErrorResponse output = 
            given()
				.spec(specification)
                    .body(productDTO)
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
        assertEquals("/api/v1/product", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(210)
    void testUpdateAsCustomer() {
        ProductUpdateDTO productDTO = new ProductUpdateDTO(1L, 1L, 
            "Product name 1", null);

        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/product/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(210)
    void testDeleteAsCustomer() {
        APIErrorResponse output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 1)
				.when()
				    .delete("/{product-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/product/1", output.instance());
        assertNull(output.errors());
    }

}
