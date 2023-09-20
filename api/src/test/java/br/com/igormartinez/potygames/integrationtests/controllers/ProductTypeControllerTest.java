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
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.ProductTypeCreateDTO;
import br.com.igormartinez.potygames.data.request.ProductTypeUpdateDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.ProductTypeDTO;
import br.com.igormartinez.potygames.data.security.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class ProductTypeControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

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

        assertEquals(4, output.size());

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

        assertEquals(3L, output.id());
        assertEquals("yugioh-booster-box", output.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", output.description());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamIdInvalid() {
        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The product-type-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/product/type/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithTypeNotFound() {
        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product type was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/type/12546", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        ProductTypeCreateDTO typeDTO 
            = new ProductTypeCreateDTO("loremipsum", "Lorem Ipsum");

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(typeDTO)
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
        assertEquals("/api/v1/product/type", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        ProductTypeUpdateDTO typeDTO 
            = new ProductTypeUpdateDTO(1L, "loremipsum", "Lorem Ipsum");

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/product/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-type-id", 1)
                    .body(typeDTO)
				.when()
				    .put("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
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
			.setBasePath("/api/v1/product/type")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
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

        assertEquals(4, output.size());

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

        assertEquals(3L, output.id());
        assertEquals("yugioh-booster-box", output.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", output.description());
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        ProductTypeCreateDTO typeDTO 
            = new ProductTypeCreateDTO("loremipsum", "Lorem ipsum");

        ProductTypeDTO output =
			given()
				.spec(specification)
					.body(typeDTO)
				.when()
					.post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductTypeDTO.class);

        assertTrue(output.id() > 0);
        assertEquals("loremipsum", output.keyword());
        assertEquals("Lorem ipsum", output.description());

        PRODUCT_TYPE_ID = output.id();
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
        assertEquals("/api/v1/product/type", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithFieldsNullOrBlank() {
        ProductTypeCreateDTO typeDTO 
            = new ProductTypeCreateDTO(null, "  ");

        APIErrorResponse output =
			given()
				.spec(specification)
                    .body(typeDTO)
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
        assertEquals("/api/v1/product/type", output.instance());
        assertEquals(2, output.errors().size());
        assertEquals("The keyword must not be blank.", output.errors().get("keyword"));
        assertEquals("The description must not be blank.", output.errors().get("description"));
    }

    @Test
    @Order(120)
    void testUpdatAsAdmin() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(PRODUCT_TYPE_ID, "dolorsit", "Dolor sit");

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

        assertEquals(PRODUCT_TYPE_ID, output.id());
        assertEquals("dolorsit", output.keyword());
        assertEquals("Dolor sit", output.description());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithoutBody() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
				.when()
					.put("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithFieldsNullOrBlank() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(-55L, " ", null);

        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
					.body(productTypeDTO)
				.when()
					.put("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
        assertEquals(3, output.errors().size());
        assertEquals("The id must be a positive number.", output.errors().get("id"));
        assertEquals("The keyword must not be blank.", output.errors().get("keyword"));
        assertEquals("The description must not be blank.", output.errors().get("description"));
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(PRODUCT_TYPE_ID, "dolosit", "Dolor sit");

        APIErrorResponse output =
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
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The product-type-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/product/type/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(PRODUCT_TYPE_ID, "dolosit", "Dolor sit");

        APIErrorResponse output =
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
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the product-type-id parameter.", output.detail());
        assertEquals("/api/v1/product/type/"+(PRODUCT_TYPE_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductNotFound() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(PRODUCT_TYPE_ID+1, "dolorsit", "Dolor sit");

        APIErrorResponse output =
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
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product type was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/type/"+(PRODUCT_TYPE_ID+1), output.instance());
        assertNull(output.errors());
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
    @Order(130)
    void testDeleteAsAdminWithParamIdInvalid() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 0)
				.when()
					.delete("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The product-type-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/product/type/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithProductNotFound() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", PRODUCT_TYPE_ID+1)
				.when()
					.delete("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The product type was not found with the given ID.", output.detail());
        assertEquals("/api/v1/product/type/"+(PRODUCT_TYPE_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithAssociatedProducts() {
        APIErrorResponse output =
			given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
				.when()
					.delete("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.CONFLICT.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Conflict", output.title());
        assertEquals(HttpStatus.CONFLICT.value(), output.status());
        assertEquals("The product type cannot be removed because it is associated with products.", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
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
			.setBasePath("/api/v1/product/type")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
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

        assertEquals(4, output.size());

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

        assertEquals(3L, output.id());
        assertEquals("yugioh-booster-box", output.keyword());
        assertEquals("Yu-Gi-Oh! Booster Box", output.description());
    }

    @Test
    @Order(210)
    void testCreateAsCustomer() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("loremipsum", "Lorem ipsum");

        APIErrorResponse output = 
            given()
				.spec(specification)
                    .body(productTypeDTO)
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
        assertEquals("/api/v1/product/type", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(210)
    void testUpdateAsCustomer() {
        ProductTypeUpdateDTO productTypeDTO 
            = new ProductTypeUpdateDTO(1L,"loremipsum", "Lorem ipsum");

        APIErrorResponse output = 
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
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(210)
    void testDeleteAsCustomer() {
        APIErrorResponse output = 
            given()
				.spec(specification)
                    .pathParam("product-type-id", 1)
				.when()
				    .delete("/{product-type-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/product/type/1", output.instance());
        assertNull(output.errors());
    }
}
