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
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Wine - Clavet Saint Emilion", outputPosition0.name());
        assertEquals("Veronica", outputPosition0.altName());
        assertEquals(new BigDecimal("74.86"), outputPosition0.price());
        assertEquals(377, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals(1L, outputPosition4.idProductType());
        assertEquals("Stock - Beef, Brown", outputPosition4.name());
        assertEquals("Bibi", outputPosition4.altName());
        assertEquals(new BigDecimal("87.91"), outputPosition4.price());
        assertEquals(307, outputPosition4.quantity());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals(7L, outputPosition9.idProductType());
        assertEquals("Wine - Harrow Estates, Vidal", outputPosition9.name());
        assertEquals("Fitzgerald", outputPosition9.altName());
        assertEquals(new BigDecimal("45.87"), outputPosition9.price());
        assertEquals(287, outputPosition9.quantity());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedPage2Size10DirectionASC() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
                    .queryParam("page", 2)
                    .queryParam("size", 10)
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
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(21L, outputPosition0.id());
        assertEquals(6L, outputPosition0.idProductType());
        assertEquals("Bread - Focaccia Quarter", outputPosition0.name());
        assertEquals("Kristin", outputPosition0.altName());
        assertEquals(new BigDecimal("77.59"), outputPosition0.price());
        assertEquals(616, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(25L, outputPosition4.id());
        assertEquals(7L, outputPosition4.idProductType());
        assertEquals("Cheese - Roquefort Pappillon", outputPosition4.name());
        assertEquals("Clare", outputPosition4.altName());
        assertEquals(new BigDecimal("25.69"), outputPosition4.price());
        assertEquals(10, outputPosition4.quantity());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(30L, outputPosition9.id());
        assertEquals(6L, outputPosition9.idProductType());
        assertEquals("Wine - Pinot Grigio Collavini", outputPosition9.name());
        assertEquals("Gordan", outputPosition9.altName());
        assertEquals(new BigDecimal("8.47"), outputPosition9.price());
        assertEquals(203, outputPosition9.quantity());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedPage1Size12DirectionASC() {
        List<ProductDTO> output = 
            given()
				.basePath("/api/v1/product")
					.port(TestConfigs.SERVER_PORT)
                    .queryParam("page", 1)
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
        assertEquals(12, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(13L, outputPosition0.id());
        assertEquals(8L, outputPosition0.idProductType());
        assertEquals("Bagel - Everything Presliced", outputPosition0.name());
        assertEquals("Rancell", outputPosition0.altName());
        assertEquals(new BigDecimal("50.52"), outputPosition0.price());
        assertEquals(562, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(17L, outputPosition4.id());
        assertEquals(8L, outputPosition4.idProductType());
        assertEquals("Lettuce - Boston Bib - Organic", outputPosition4.name());
        assertEquals("Melisenda", outputPosition4.altName());
        assertEquals(new BigDecimal("16.86"), outputPosition4.price());
        assertEquals(928, outputPosition4.quantity());

        ProductDTO outputPosition11 = output.get(11);
        assertEquals(24L, outputPosition11.id());
        assertEquals(10L, outputPosition11.idProductType());
        assertEquals("Tia Maria", outputPosition11.name());
        assertEquals("Rosamund", outputPosition11.altName());
        assertEquals(new BigDecimal("29.79"), outputPosition11.price());
        assertEquals(65, outputPosition11.quantity());
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
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1000L, outputPosition0.id());
        assertEquals(10L, outputPosition0.idProductType());
        assertEquals("Ketchup - Tomato", outputPosition0.name());
        assertEquals("Sandor", outputPosition0.altName());
        assertEquals(new BigDecimal("57.28"), outputPosition0.price());
        assertEquals(745, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(996L, outputPosition4.id());
        assertEquals(7L, outputPosition4.idProductType());
        assertEquals("Mountain Dew", outputPosition4.name());
        assertEquals("Nevins", outputPosition4.altName());
        assertEquals(new BigDecimal("91.98"), outputPosition4.price());
        assertEquals(636, outputPosition4.quantity());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(991L, outputPosition9.id());
        assertEquals(3L, outputPosition9.idProductType());
        assertEquals("Pepper - Black, Whole", outputPosition9.name());
        assertEquals("Aeriela", outputPosition9.altName());
        assertEquals(new BigDecimal("47.43"), outputPosition9.price());
        assertEquals(680, outputPosition9.quantity());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        ProductDTO output = 
            given()
				.basePath("/api/v1/product")
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
        assertEquals("Request object cannot be null", output.getDetail());
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
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/v1/product/12546", output.getInstance());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        ProductDTO productDTO = new ProductDTO(null, 1L, 
            "Product name 1", null, new BigDecimal("10.99"), 25);

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
            "Product name 1", null, new BigDecimal("10.99"), 25);

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
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Wine - Clavet Saint Emilion", outputPosition0.name());
        assertEquals("Veronica", outputPosition0.altName());
        assertEquals(new BigDecimal("74.86"), outputPosition0.price());
        assertEquals(377, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals(1L, outputPosition4.idProductType());
        assertEquals("Stock - Beef, Brown", outputPosition4.name());
        assertEquals("Bibi", outputPosition4.altName());
        assertEquals(new BigDecimal("87.91"), outputPosition4.price());
        assertEquals(307, outputPosition4.quantity());

        ProductDTO outputPosition17 = output.get(9);
        assertEquals(10L, outputPosition17.id());
        assertEquals(7L, outputPosition17.idProductType());
        assertEquals("Wine - Harrow Estates, Vidal", outputPosition17.name());
        assertEquals("Fitzgerald", outputPosition17.altName());
        assertEquals(new BigDecimal("45.87"), outputPosition17.price());
        assertEquals(287, outputPosition17.quantity());
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
        assertEquals("/api/v1/product", output.getInstance());
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
        assertEquals("/api/v1/product", output.getInstance());
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
        assertEquals("/api/v1/product/0", output.getInstance());
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
        assertEquals("/api/v1/product/1024", output.getInstance());
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
        assertEquals("/api/v1/product/1", output.getInstance());
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
        assertEquals("/api/v1/product/"+PRODUCT_ID, output.getInstance());
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
        assertEquals("Request object cannot be null", output.getDetail());
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
        assertEquals("The resource was not found", output.getDetail());
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
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Wine - Clavet Saint Emilion", outputPosition0.name());
        assertEquals("Veronica", outputPosition0.altName());
        assertEquals(new BigDecimal("74.86"), outputPosition0.price());
        assertEquals(377, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals(1L, outputPosition4.idProductType());
        assertEquals("Stock - Beef, Brown", outputPosition4.name());
        assertEquals("Bibi", outputPosition4.altName());
        assertEquals(new BigDecimal("87.91"), outputPosition4.price());
        assertEquals(307, outputPosition4.quantity());

        ProductDTO outputPosition17 = output.get(9);
        assertEquals(10L, outputPosition17.id());
        assertEquals(7L, outputPosition17.idProductType());
        assertEquals("Wine - Harrow Estates, Vidal", outputPosition17.name());
        assertEquals("Fitzgerald", outputPosition17.altName());
        assertEquals(new BigDecimal("45.87"), outputPosition17.price());
        assertEquals(287, outputPosition17.quantity());
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
        assertEquals("/api/v1/product", output.getInstance());
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
