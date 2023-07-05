package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
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
public class ProductTypeControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";
    private static String CUSTOMER_EMAIL = "fragge1@blinklist.com";
    private static String CUSTOMER_PASSWORD = "ZkIfFOo";

    private static Long PRODUCT_TYPE_ID;
 
    @Test
    @Order(0)
    void testFindAllAsUnauthenticated() {
        List<ProductTypeDTO> output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(3, output.size());

        ProductTypeDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("board-game", outputPosition0.keyword());
        assertEquals("Board Games", outputPosition0.description());

        ProductTypeDTO outputPosition2 = output.get(2);
        assertEquals(3L, outputPosition2.id());
        assertEquals("yugioh-booster-box", outputPosition2.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", outputPosition2.description());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        ProductTypeDTO output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-type-id", 3)
				.when()
				    .get("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(3L, output.id());
        assertEquals("yugioh-booster-box", output.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", output.description());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamIdInvalid() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-type-id", 0)
				.when()
				    .get("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product-type-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/type/0", output.getInstance());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithTypeNotFound() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-type-id", 12546)
				.when()
				    .get("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/12546", output.getInstance());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, 
            "loremipsum", "Lorem ipsum");

        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(productTypeDTO)
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
        assertEquals("/api/v1/product/type", output.getInstance());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(1L, 
            "loremipsum", "Lorem ipsum");

        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-type-id", 1)
                    .body(productTypeDTO)
				.when()
				    .put("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/1", output.getInstance());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-type-id", 1)
				.when()
				    .delete("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/1", output.getInstance());
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
			.setBasePath("/api/v1/product/type")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(101)
    void testFindAllAsAdmin() {
        List<ProductTypeDTO> output = 
            given()
				.spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(3, output.size());

        ProductTypeDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("board-game", outputPosition0.keyword());
        assertEquals("Board Games", outputPosition0.description());

        ProductTypeDTO outputPosition2 = output.get(2);
        assertEquals(3L, outputPosition2.id());
        assertEquals("yugioh-booster-box", outputPosition2.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", outputPosition2.description());
    }

    @Test
    @Order(101)
    void testFindByIdAsAdmin() {
        ProductTypeDTO output = 
            given()
				.spec(specification)
                    .pathParam("product-type-id", 3)
				.when()
				    .get("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(3L, output.id());
        assertEquals("yugioh-booster-box", output.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", output.description());
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, 
            "loremipsum", "Lorem ipsum");

        ProductTypeDTO output =
			given()
				.spec(specification)
					.body(productTypeDTO)
				.when()
					.post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductTypeDTO.class);

        assertNotNull(output);
        assertTrue(output.id() > 0);
        assertEquals("loremipsum", output.keyword());
        assertEquals("Lorem ipsum", output.description());

        PRODUCT_TYPE_ID = output.id();
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithBadTypeRequest() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, null, null);

        ExceptionResponse output =
			given()
				.spec(specification)
					.body(productTypeDTO)
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
        assertEquals("The keyword of product type must not be blank.", output.getDetail());
        assertEquals("/api/v1/product/type", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdatAsAdmin() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(PRODUCT_TYPE_ID, "dolorsit", "Dolor sit");

        ProductTypeDTO output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", PRODUCT_TYPE_ID)
					.body(productTypeDTO)
				.when()
					.put("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(PRODUCT_TYPE_ID, output.id());
        assertEquals("dolorsit", output.keyword());
        assertEquals("Dolor sit", output.description());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(PRODUCT_TYPE_ID, 
            "dolosit", "Dolor sit");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 0)
					.body(productTypeDTO)
				.when()
					.put("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product-type-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/type/0", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(PRODUCT_TYPE_ID, 
            "dolosit", "Dolor sit");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", PRODUCT_TYPE_ID+1)
					.body(productTypeDTO)
				.when()
					.put("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The ID in the request body must match the value of the product-type-id parameter.", output.getDetail());
        assertEquals("/api/v1/product/type/"+(PRODUCT_TYPE_ID+1), output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductNotFound() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(PRODUCT_TYPE_ID+1, "dolorsit", "Dolor sit");

        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", PRODUCT_TYPE_ID+1)
					.body(productTypeDTO)
				.when()
					.put("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/"+(PRODUCT_TYPE_ID+1), output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithParamIdInvalid() {
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 0)
				.when()
					.delete("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The product-type-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/type/0", output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithProductNotFound() {
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", PRODUCT_TYPE_ID+1)
				.when()
					.delete("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/"+(PRODUCT_TYPE_ID+1), output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithAssociatedProducts() {
        ExceptionResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
				.when()
					.delete("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.CONFLICT.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Conflict", output.getTitle());
        assertEquals(HttpStatus.CONFLICT.value(), output.getStatus().intValue());
        assertEquals("The product type cannot be removed because it is associated with products.", output.getDetail());
        assertEquals("/api/v1/product/type/1", output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdmin() {
		given()
			.spec(specification)
                    .pathParam("product-type-id", PRODUCT_TYPE_ID)
				.when()
					.delete("/{product-type-id}")
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
			.setBasePath("/api/v1/product/type")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(201)
    void testFindAllAsCustomer() {
        List<ProductTypeDTO> output = 
            given()
				.spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(3, output.size());

        ProductTypeDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("board-game", outputPosition0.keyword());
        assertEquals("Board Games", outputPosition0.description());

        ProductTypeDTO outputPosition2 = output.get(2);
        assertEquals(3L, outputPosition2.id());
        assertEquals("yugioh-booster-box", outputPosition2.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", outputPosition2.description());
    }

    @Test
    @Order(201)
    void testFindByIdAsCustomer() {
        ProductTypeDTO output = 
            given()
				.spec(specification)
                    .pathParam("product-type-id", 3)
				.when()
				    .get("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductTypeDTO.class);

        assertNotNull(output);
        assertEquals(3L, output.id());
        assertEquals("yugioh-booster-box", output.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", output.description());
    }

    @Test
    @Order(210)
    void testCreateAsCustomer() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, "loremipsum", "Lorem ipsum");

        ExceptionResponse output = 
            given()
				.spec(specification)
                    .body(productTypeDTO)
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
        assertEquals("/api/v1/product/type", output.getInstance());
    }

    @Test
    @Order(210)
    void testUpdateAsCustomer() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(1L, "loremipsum", "Lorem ipsum");

        ExceptionResponse output = 
            given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
                    .body(productTypeDTO)
				.when()
				    .put("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/1", output.getInstance());
    }

    @Test
    @Order(210)
    void testDeleteAsCustomer() {
        ExceptionResponse output = 
            given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
				.when()
				    .delete("/{product-type-id}")
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
        assertEquals("/api/v1/product/type/1", output.getInstance());
    }
}
