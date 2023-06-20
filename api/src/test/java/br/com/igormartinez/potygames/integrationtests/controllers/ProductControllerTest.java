package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
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
    void testFindAllAsUnauthenticated() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/product/v1")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", ProductDTO.class);

        assertNotNull(output);
        assertEquals(18, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Wine - Clavet Saint Emilion", outputPosition0.name());
        assertEquals("Veronica", outputPosition0.altName());
        assertEquals(new BigDecimal("74.86"), outputPosition0.price());
        assertEquals(377, outputPosition0.quantity());

        ProductDTO outputPosition8 = output.get(8);
        assertEquals(9L, outputPosition8.id());
        assertEquals(5L, outputPosition8.idProductType());
        assertEquals("Ice Cream - Life Savers", outputPosition8.name());
        assertEquals("Minna", outputPosition8.altName());
        assertEquals(new BigDecimal("84.48"), outputPosition8.price());
        assertEquals(149, outputPosition8.quantity());

        ProductDTO outputPosition17 = output.get(17);
        assertEquals(18L, outputPosition17.id());
        assertEquals(7L, outputPosition17.idProductType());
        assertEquals("Beans - Soya Bean", outputPosition17.name());
        assertEquals("Vinnie", outputPosition17.altName());
        assertEquals(new BigDecimal("2.91"), outputPosition17.price());
        assertEquals(307, outputPosition17.quantity());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        ProductDTO output = 
            given()
				.basePath("/api/product/v1")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("product-id", 15)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(15L, output.id());
        assertEquals(10L, output.idProductType());
        assertEquals("Bouillion - Fish", output.name());
        assertEquals("Wilona", output.altName());
        assertEquals(new BigDecimal("3.98"), output.price());
        assertEquals(116, output.quantity());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamIdInvalid() {
        ExceptionResponse output = 
            given()
				.basePath("/api/product/v1")
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
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/product/v1/0", output.getInstance());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithProductNotFound() {
        ExceptionResponse output = 
            given()
				.basePath("/api/product/v1")
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
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/product/v1/12546", output.getInstance());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

        ExceptionResponse output = 
            given()
				.basePath("/api/product/v1")
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
        assertEquals("/api/product/v1", output.getInstance());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        ProductDTO productDTO = new ProductDTO(1L, 1L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

        ExceptionResponse output = 
            given()
				.basePath("/api/product/v1")
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
        assertEquals("/api/product/v1/1", output.getInstance());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        ExceptionResponse output = 
            given()
				.basePath("/api/product/v1")
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
        assertEquals("/api/product/v1/1", output.getInstance());
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
			.setBasePath("/api/product/v1")
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
                                    .getList(".", ProductDTO.class);

        assertNotNull(output);
        assertEquals(18, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Wine - Clavet Saint Emilion", outputPosition0.name());
        assertEquals("Veronica", outputPosition0.altName());
        assertEquals(new BigDecimal("74.86"), outputPosition0.price());
        assertEquals(377, outputPosition0.quantity());

        ProductDTO outputPosition8 = output.get(8);
        assertEquals(9L, outputPosition8.id());
        assertEquals(5L, outputPosition8.idProductType());
        assertEquals("Ice Cream - Life Savers", outputPosition8.name());
        assertEquals("Minna", outputPosition8.altName());
        assertEquals(new BigDecimal("84.48"), outputPosition8.price());
        assertEquals(149, outputPosition8.quantity());

        ProductDTO outputPosition17 = output.get(17);
        assertEquals(18L, outputPosition17.id());
        assertEquals(7L, outputPosition17.idProductType());
        assertEquals("Beans - Soya Bean", outputPosition17.name());
        assertEquals("Vinnie", outputPosition17.altName());
        assertEquals(new BigDecimal("2.91"), outputPosition17.price());
        assertEquals(307, outputPosition17.quantity());
    }

    @Test
    @Order(101)
    void testFindByIdAsAdmin() {
        ProductDTO output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 15)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(15L, output.id());
        assertEquals(10L, output.idProductType());
        assertEquals("Bouillion - Fish", output.name());
        assertEquals("Wilona", output.altName());
        assertEquals(new BigDecimal("3.98"), output.price());
        assertEquals(116, output.quantity());
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

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
        assertNull(output.altName());
        assertEquals(new BigDecimal("10.99"), output.price());
        assertEquals(25, output.quantity());

        PRODUCT_ID = output.id();
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithBadProductRequest() {
        ProductDTO productDTO = new ProductDTO(null, null, 
            null, null, null, null);

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
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/product/v1", output.getInstance());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithNotFoundProductType() {
        ProductDTO productDTO = new ProductDTO(null, 15555L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

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
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/product/v1", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdatAsAdmin() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID, 2L, 
            "Product updated name 1", "Alt name 1", new BigDecimal("1.99"), 22);

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
        assertEquals("Alt name 1", output.altName());
        assertEquals(new BigDecimal("1.99"), output.price());
        assertEquals(22, output.quantity());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        ProductDTO productDTO = new ProductDTO(1L, 1L, 
            "Product name updated", "Alt name", new BigDecimal("1.99"), 20);

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
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/product/v1/0", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        ProductDTO productDTO = new ProductDTO(1L, 1L, 
            "Product name updated", "Alt name", new BigDecimal("1.99"), 20);

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
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/product/v1/1024", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamDTOInvalid() {
        ProductDTO productDTO = new ProductDTO(1L, null, 
            null, "Alt name", null, null);

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
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/product/v1/1", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductTypeNotFound() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID, 1024L, 
            "Product updated name 1", "Alt name 1", new BigDecimal("1.99"), 22);

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
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/product/v1/"+PRODUCT_ID, output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithProductNotFound() {
        ProductDTO productDTO = new ProductDTO(PRODUCT_ID+1, 2L, 
            "Product updated name 1", "Alt name 1", new BigDecimal("1.99"), 22);

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
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/product/v1/"+(PRODUCT_ID+1), output.getInstance());
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
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/product/v1/0", output.getInstance());
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
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/product/v1/"+(PRODUCT_ID+1), output.getInstance());
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
			.setBasePath("/api/product/v1")
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
                                    .getList(".", ProductDTO.class);

        assertNotNull(output);
        assertEquals(18, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Wine - Clavet Saint Emilion", outputPosition0.name());
        assertEquals("Veronica", outputPosition0.altName());
        assertEquals(new BigDecimal("74.86"), outputPosition0.price());
        assertEquals(377, outputPosition0.quantity());

        ProductDTO outputPosition8 = output.get(8);
        assertEquals(9L, outputPosition8.id());
        assertEquals(5L, outputPosition8.idProductType());
        assertEquals("Ice Cream - Life Savers", outputPosition8.name());
        assertEquals("Minna", outputPosition8.altName());
        assertEquals(new BigDecimal("84.48"), outputPosition8.price());
        assertEquals(149, outputPosition8.quantity());

        ProductDTO outputPosition17 = output.get(17);
        assertEquals(18L, outputPosition17.id());
        assertEquals(7L, outputPosition17.idProductType());
        assertEquals("Beans - Soya Bean", outputPosition17.name());
        assertEquals("Vinnie", outputPosition17.altName());
        assertEquals(new BigDecimal("2.91"), outputPosition17.price());
        assertEquals(307, outputPosition17.quantity());
    }

    @Test
    @Order(201)
    void testFindByIdAsCustomer() {
        ProductDTO output = 
            given()
				.spec(specification)
                    .pathParam("product-id", 15)
				.when()
				    .get("/{product-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(ProductDTO.class);

        assertNotNull(output);
        assertEquals(15L, output.id());
        assertEquals(10L, output.idProductType());
        assertEquals("Bouillion - Fish", output.name());
        assertEquals("Wilona", output.altName());
        assertEquals(new BigDecimal("3.98"), output.price());
        assertEquals(116, output.quantity());
    }

    @Test
    @Order(210)
    void testCreateAsCustomer() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

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
        assertEquals("/api/product/v1", output.getInstance());
    }

    @Test
    @Order(210)
    void testUpdateAsCustomer() {
        ProductDTO productDTO = new ProductDTO(1L, 1L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

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
        assertEquals("/api/product/v1/1", output.getInstance());
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
        assertEquals("/api/product/v1/1", output.getInstance());
    }

}
