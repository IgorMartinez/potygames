package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.potygames.models.YugiohCardCategory;
import br.com.igormartinez.potygames.models.YugiohCardType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class YugiohCardControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";
    private static String CUSTOMER_EMAIL = "fragge1@blinklist.com";
    private static String CUSTOMER_PASSWORD = "ZkIfFOo";

    private static Long CARD_ID;

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedWithoutQueryParams() {
        List<YugiohCardDTO> output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("\"A\" Cell Breeding Device", outputPosition0.name());
        assertEquals(29L, outputPosition0.category());
        assertNull(outputPosition0.type());
        assertNull(outputPosition0.attribute());
        assertNull(outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("During each of your Standby Phases"));
        assertNull(outputPosition0.pendulumScale());
        assertNull(outputPosition0.linkArrows());
        assertNull(outputPosition0.atk());
        assertNull(outputPosition0.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals("3-Hump Lacooda", outputPosition9.name());
        assertEquals(1L, outputPosition9.category());
        assertEquals(2L, outputPosition9.type());
        assertEquals("EARTH", outputPosition9.attribute());
        assertEquals(3, outputPosition9.levelRankLink());
        assertTrue(outputPosition9.effectLoreText().startsWith("If there are 3 face-up"));
        assertNull(outputPosition9.pendulumScale());
        assertNull(outputPosition9.linkArrows());
        assertEquals(500, outputPosition9.atk());
        assertEquals(1500, outputPosition9.def());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedWithPage1Size10DirectionASC() {
        List<YugiohCardDTO> output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .queryParam("direction", "asc")
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(11L, outputPosition0.id());
        assertEquals("30,000-Year White Turtle", outputPosition0.name());
        assertEquals(4L, outputPosition0.category());
        assertEquals(1L, outputPosition0.type());
        assertEquals("WATER", outputPosition0.attribute());
        assertEquals(5, outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("A huge turtle that has existed"));
        assertNull(outputPosition0.pendulumScale());
        assertNull(outputPosition0.linkArrows());
        assertEquals(1250, outputPosition0.atk());
        assertEquals(2100, outputPosition0.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(20L, outputPosition9.id());
        assertEquals("A Feint Plan", outputPosition9.name());
        assertEquals(32L, outputPosition9.category());
        assertNull(outputPosition9.type());
        assertNull(outputPosition9.attribute());
        assertNull(outputPosition9.levelRankLink());
        assertTrue(outputPosition9.effectLoreText().startsWith("A player cannot attack face-down"));
        assertNull(outputPosition9.pendulumScale());
        assertNull(outputPosition9.linkArrows());
        assertNull(outputPosition9.atk());
        assertNull(outputPosition9.def());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedWithPage0Size15DirectionASC() {
        List<YugiohCardDTO> output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .queryParam("page", 0)
                    .queryParam("size", 15)
                    .queryParam("direction", "asc")
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(15, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("\"A\" Cell Breeding Device", outputPosition0.name());
        assertEquals(29L, outputPosition0.category());
        assertNull(outputPosition0.type());
        assertNull(outputPosition0.attribute());
        assertNull(outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("During each of your Standby Phases"));
        assertNull(outputPosition0.pendulumScale());
        assertNull(outputPosition0.linkArrows());
        assertNull(outputPosition0.atk());
        assertNull(outputPosition0.def());

        YugiohCardDTO outputPosition14 = output.get(14);
        assertEquals(15L, outputPosition14.id());
        assertEquals("7 Completed", outputPosition14.name());
        assertEquals(28L, outputPosition14.category());
        assertNull(outputPosition14.type());
        assertNull(outputPosition14.attribute());
        assertNull(outputPosition14.levelRankLink());
        assertTrue(outputPosition14.effectLoreText().startsWith("Activate this card by choosing ATK or DEF"));
        assertNull(outputPosition14.pendulumScale());
        assertNull(outputPosition14.linkArrows());
        assertNull(outputPosition14.atk());
        assertNull(outputPosition14.def());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedWithPage0Size10DirectionDESC() {
        List<YugiohCardDTO> output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .queryParam("direction", "desc")
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(12649L, outputPosition0.id());
        assertEquals("ZW - Unicorn Spear", outputPosition0.name());
        assertEquals(1L, outputPosition0.category());
        assertEquals(2L, outputPosition0.type());
        assertEquals("LIGHT", outputPosition0.attribute());
        assertEquals(4, outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("You can target 1 \"Number C39:"));
        assertNull(outputPosition0.pendulumScale());
        assertNull(outputPosition0.linkArrows());
        assertEquals(1900, outputPosition0.atk());
        assertEquals(0, outputPosition0.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(12640L, outputPosition9.id());
        assertEquals("ZW - Eagle Claw", outputPosition9.name());
        assertEquals(1L, outputPosition9.category());
        assertEquals(24L, outputPosition9.type());
        assertEquals("WIND", outputPosition9.attribute());
        assertEquals(5, outputPosition9.levelRankLink());
        assertTrue(outputPosition9.effectLoreText().startsWith("If your opponent's Life Points are at least 2000"));
        assertNull(outputPosition9.pendulumScale());
        assertNull(outputPosition9.linkArrows());
        assertEquals(2000, outputPosition9.atk());
        assertEquals(1200, outputPosition9.def());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticatedWithPage15Size15DirectionDESC() {
        List<YugiohCardDTO> output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .queryParam("page", 15)
                    .queryParam("size", 15)
                    .queryParam("direction", "desc")
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(15, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(12424L, outputPosition0.id());
        assertEquals("Xtra HERO Wonder Driver", outputPosition0.name());
        assertEquals(19L, outputPosition0.category());
        assertEquals(23L, outputPosition0.type());
        assertEquals("LIGHT", outputPosition0.attribute());
        assertEquals(2, outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("2 \"HERO\" monsters"));
        assertNull(outputPosition0.pendulumScale());
        assertEquals(2, outputPosition0.linkArrows().size());
        assertTrue(outputPosition0.linkArrows().containsAll(Arrays.asList("N","S")));
        assertEquals(1900, outputPosition0.atk());
        assertNull(outputPosition0.def());

        YugiohCardDTO outputPosition14 = output.get(14);
        assertEquals(12410L, outputPosition14.id());
        assertEquals("X-Saber Palomuro", outputPosition14.name());
        assertEquals(16L, outputPosition14.category());
        assertEquals(18L, outputPosition14.type());
        assertEquals("EARTH", outputPosition14.attribute());
        assertEquals(1, outputPosition14.levelRankLink());
        assertTrue(outputPosition14.effectLoreText().startsWith("When another \"Saber\" monster you control"));
        assertNull(outputPosition14.pendulumScale());
        assertNull(outputPosition14.linkArrows());
        assertEquals(200, outputPosition14.atk());
        assertEquals(300, outputPosition14.def());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        YugiohCardDTO output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("yugioh-card-id", 2061)
				.when()
				    .get("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(YugiohCardDTO.class);
        
        assertEquals(2061L, output.id());
        assertEquals("Crystal Beast Sapphire Pegasus", output.name());
        assertEquals(1L, output.category());
        assertEquals(2L, output.type());
        assertEquals("WIND", output.attribute());
        assertEquals(4, output.levelRankLink());
        assertTrue(output.effectLoreText().startsWith("When this card is Summoned: You can place"));
        assertNull(output.pendulumScale());
        assertNull(output.linkArrows());
        assertEquals(1800, output.atk());
        assertEquals(1200, output.def());
    }
    
    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithParamIdInvalid() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("yugioh-card-id", 0)
				.when()
				    .get("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The yugioh-card-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/0", output.getInstance());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticatedWithCardNotFound() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("yugioh-card-id", 12700)
				.when()
				    .get("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The card was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/12700", output.getInstance());
    }

    @Test
    @Order(0)
    void testCreateAsUnauthenticated() {
        List<String> linkArrows = Arrays.asList("W", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Onyx Gorilla", 19L, 2L, "EARTH", 
            2, "Effect lore text...", null, 
            linkArrows, 2000, null);

        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(cardDTO)
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
        assertEquals("/api/v1/product/yugioh-card", output.getInstance());
    }

    @Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
        List<String> linkArrows = Arrays.asList("SW", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            1L, "Crystal Beast Onyx Gorilla", 1L, 1L, "EARTH", 
            2, "Effect lore text", null, 
            linkArrows, 2000, 1000);

        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("yugioh-card-id", 1)
                    .body(cardDTO)
				.when()
				    .put("/{yugioh-card-id}")
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
        assertEquals("/api/v1/product/yugioh-card/1", output.getInstance());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        ExceptionResponse output = 
            given()
				.basePath("/api/v1/product/yugioh-card")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("yugioh-card-id", 1)
				.when()
				    .delete("/{yugioh-card-id}")
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
        assertEquals("/api/v1/product/yugioh-card/1", output.getInstance());
    }

    @Test
    @Order(0)
    void testFindAllAttributesAsUnauthenticated() {
        List<String> output = 
            given()
				.basePath("/api/v1/product/yugioh-card/attribute")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", String.class);

        assertEquals(7, output.size());
        assertEquals("DARK", output.get(0));
        assertEquals("WIND", output.get(6));
    }

    @Test
    @Order(0)
    void testFindAllCategoriesAsUnauthenticated() {
        List<YugiohCardCategory> output = 
            given()
				.basePath("/api/v1/product/yugioh-card/category")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", YugiohCardCategory.class);

        assertEquals(36, output.size());

        YugiohCardCategory outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.getId());
        assertEquals("Monster", outputPosition0.getCategory());
        assertEquals("Effect", outputPosition0.getSubCategory());
        assertTrue(outputPosition0.getMainDeck());
        assertNull(outputPosition0.getYugiohCards());

        YugiohCardCategory outputPosition35 = output.get(35);
        assertEquals(36L, outputPosition35.getId());
        assertEquals("Others", outputPosition35.getCategory());
        assertEquals("Token", outputPosition35.getSubCategory());
        assertFalse(outputPosition35.getMainDeck());
        assertNull(outputPosition35.getYugiohCards());
    }

    @Test
    @Order(0)
    void testFindAllTypesAsUnauthenticated() {
        List<YugiohCardType> output = 
            given()
				.basePath("/api/v1/product/yugioh-card/type")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", YugiohCardType.class);

        assertEquals(26, output.size());

        YugiohCardType outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.getId());
        assertEquals("Aqua", outputPosition0.getDescription());
        assertNull(outputPosition0.getYugiohCards());

        YugiohCardType outputPosition25 = output.get(25);
        assertEquals(26L, outputPosition25.getId());
        assertEquals("Zombie", outputPosition25.getDescription());
        assertNull(outputPosition25.getYugiohCards());
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
			.setBasePath("/api/v1/product/yugioh-card")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(101)
    void testFindAllAsAdmin() {
        List<YugiohCardDTO> output = 
            given()
				.spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("\"A\" Cell Breeding Device", outputPosition0.name());
        assertEquals(29L, outputPosition0.category());
        assertNull(outputPosition0.type());
        assertNull(outputPosition0.attribute());
        assertNull(outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("During each of your Standby Phases"));
        assertNull(outputPosition0.pendulumScale());
        assertNull(outputPosition0.linkArrows());
        assertNull(outputPosition0.atk());
        assertNull(outputPosition0.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals("3-Hump Lacooda", outputPosition9.name());
        assertEquals(1L, outputPosition9.category());
        assertEquals(2L, outputPosition9.type());
        assertEquals("EARTH", outputPosition9.attribute());
        assertEquals(3, outputPosition9.levelRankLink());
        assertTrue(outputPosition9.effectLoreText().startsWith("If there are 3 face-up"));
        assertNull(outputPosition9.pendulumScale());
        assertNull(outputPosition9.linkArrows());
        assertEquals(500, outputPosition9.atk());
        assertEquals(1500, outputPosition9.def());
    }

    @Test
    @Order(101)
    void testFindByIdAsAdmin() {
        YugiohCardDTO output = 
            given()
				.spec(specification)
                    .pathParam("yugioh-card-id", 2061)
				.when()
				    .get("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(YugiohCardDTO.class);
        
        assertEquals(2061L, output.id());
        assertEquals("Crystal Beast Sapphire Pegasus", output.name());
        assertEquals(1L, output.category());
        assertEquals(2L, output.type());
        assertEquals("WIND", output.attribute());
        assertEquals(4, output.levelRankLink());
        assertTrue(output.effectLoreText().startsWith("When this card is Summoned: You can place"));
        assertNull(output.pendulumScale());
        assertNull(output.linkArrows());
        assertEquals(1800, output.atk());
        assertEquals(1200, output.def());
    }

    @Test
    @Order(110)
    void testCreateAsAdmin() {
        List<String> linkArrows = Arrays.asList("W", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Onyx Gorilla", 19L, 2L, "EARTH", 
            2, "Effect lore text...", null, 
            linkArrows, 2000, null);

        YugiohCardDTO output = 
            given()
				.spec(specification)
                    .body(cardDTO)
				.when()
				    .post()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(YugiohCardDTO.class);

        assertTrue(output.id() > 0);
        assertEquals("Crystal Beast Onyx Gorilla", output.name());
        assertEquals(19L, output.category());
        assertEquals(2L, output.type());
        assertEquals("EARTH", output.attribute());
        assertEquals(2, output.levelRankLink());
        assertEquals("Effect lore text...", output.effectLoreText());
        assertNull(output.pendulumScale());
        assertEquals(2, output.linkArrows().size());
        assertTrue(output.linkArrows().containsAll(Arrays.asList("W", "S")));
        assertEquals(2000, output.atk());
        assertNull(output.def());

        CARD_ID = output.id();
    }
    
    @Test
    @Order(110)
    void testCreateAsAdminWithBadCardRequest() {
        List<String> linkArrows = Arrays.asList("W", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, null, 19L, 2L, "EARTH", 
            2, "Effect lore text...", null, 
            linkArrows, 2000, null);

        ExceptionResponse output =
			given()
				.spec(specification)
					.body(cardDTO)
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
        assertEquals("The card name must not be blank.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card", output.getInstance());
    }

    @Test
    @Order(110)
    void testCreateAsAdminWithCategoryNotFound() {
        List<String> linkArrows = Arrays.asList("W", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Onyx Gorilla", 50L, 2L, "EARTH", 
            2, "Effect lore text...", null, 
            linkArrows, 2000, null);

        ExceptionResponse output =
			given()
				.spec(specification)
					.body(cardDTO)
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
        assertEquals("The card category was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdmin() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            CARD_ID, "Crystal Scout", 24L, 2L, "WATER", 
            4, "Effect lore updated text...", null, 
            null, 1800, 2400);

        YugiohCardDTO output = 
            given()
				.spec(specification)
                    .pathParam("yugioh-card-id", CARD_ID)
                    .body(cardDTO)
				.when()
				    .put("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(YugiohCardDTO.class);

        assertEquals(CARD_ID, output.id());
        assertEquals("Crystal Scout", output.name());
        assertEquals(24L, output.category());
        assertEquals(2L, output.type());
        assertEquals("WATER", output.attribute());
        assertEquals(4, output.levelRankLink());
        assertEquals("Effect lore updated text...", output.effectLoreText());
        assertNull(output.pendulumScale());
        assertNull(output.linkArrows());
        assertEquals(1800, output.atk());
        assertEquals(2400, output.def());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithParamIdInvalid() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            CARD_ID, "Crystal Scout", 24L, 2L, "WATER", 
            4, "Effect lore updated text...", null, 
            null, 1800, 2400);

        ExceptionResponse output =
			given()
				.spec(specification)
                .pathParam("yugioh-card-id", 0)
					.body(cardDTO)
				.when()
					.put("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The yugioh-card-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/0", output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithMismatchDTOIdAndParamId() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            CARD_ID, "Crystal Scout", 24L, 2L, "WATER", 
            4, "Effect lore updated text...", null, 
            null, 1800, 2400);

        ExceptionResponse output =
			given()
				.spec(specification)
                .pathParam("yugioh-card-id", CARD_ID+1)
					.body(cardDTO)
				.when()
					.put("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The ID in the request body must match the value of the yugioh-card-id parameter.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/"+(CARD_ID+1), output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithTypeNotFound() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            CARD_ID, "Crystal Scout", 24L, 20000L, "WATER", 
            4, "Effect lore updated text...", null, 
            null, 1800, 2400);

        ExceptionResponse output =
			given()
				.spec(specification)
                .pathParam("yugioh-card-id", CARD_ID)
					.body(cardDTO)
				.when()
					.put("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The card type was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/"+CARD_ID, output.getInstance());
    }

    @Test
    @Order(120)
    void testUpdateAsAdminWithCardNotFound() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            CARD_ID+1, "Crystal Scout", 24L, 2L, "WATER", 
            4, "Effect lore updated text...", null, 
            null, 1800, 2400);

        ExceptionResponse output =
			given()
				.spec(specification)
                .pathParam("yugioh-card-id", CARD_ID+1)
					.body(cardDTO)
				.when()
					.put("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The card was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/"+(CARD_ID+1), output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdmin() {
		given()
			.spec(specification)
            .pathParam("yugioh-card-id", CARD_ID)
			.when()
				.delete("/{yugioh-card-id}")
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
                .pathParam("yugioh-card-id", 0)
				.when()
					.delete("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("The yugioh-card-id must be a positive integer value.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/0", output.getInstance());
    }

    @Test
    @Order(130)
    void testDeleteAsAdminWithCardNotFound() {
        ExceptionResponse output =
			given()
				.spec(specification)
                .pathParam("yugioh-card-id", CARD_ID+1)
				.when()
					.delete("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
                                .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The card was not found with the given ID.", output.getDetail());
        assertEquals("/api/v1/product/yugioh-card/"+(CARD_ID+1), output.getInstance());
    }

    @Test
    @Order(140)
    void testFindAllAttributesAsAdmin() {
        List<String> output = 
            given()
				.spec(specification)
				.when()
				    .get("/attribute")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", String.class);

        assertEquals(7, output.size());
        assertEquals("DARK", output.get(0));
        assertEquals("WIND", output.get(6));
    }

    @Test
    @Order(140)
    void testFindAllCategoriesAsAdmin() {
        List<YugiohCardCategory> output = 
            given()
				.spec(specification)
				.when()
				    .get("/category")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", YugiohCardCategory.class);

        assertEquals(36, output.size());

        YugiohCardCategory outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.getId());
        assertEquals("Monster", outputPosition0.getCategory());
        assertEquals("Effect", outputPosition0.getSubCategory());
        assertTrue(outputPosition0.getMainDeck());
        assertNull(outputPosition0.getYugiohCards());

        YugiohCardCategory outputPosition35 = output.get(35);
        assertEquals(36L, outputPosition35.getId());
        assertEquals("Others", outputPosition35.getCategory());
        assertEquals("Token", outputPosition35.getSubCategory());
        assertFalse(outputPosition35.getMainDeck());
        assertNull(outputPosition35.getYugiohCards());
    }

    @Test
    @Order(140)
    void testFindAllTypesAsAdmin() {
        List<YugiohCardType> output = 
            given()
				.spec(specification)
				.when()
				    .get("/type")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", YugiohCardType.class);

        assertEquals(26, output.size());

        YugiohCardType outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.getId());
        assertEquals("Aqua", outputPosition0.getDescription());
        assertNull(outputPosition0.getYugiohCards());

        YugiohCardType outputPosition25 = output.get(25);
        assertEquals(26L, outputPosition25.getId());
        assertEquals("Zombie", outputPosition25.getDescription());
        assertNull(outputPosition25.getYugiohCards());
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
			.setBasePath("/api/v1/product/yugioh-card")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(201)
    void testFindAllAsCustomer() {
        List<YugiohCardDTO> output = 
            given()
				.spec(specification)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList("content", YugiohCardDTO.class);

        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("\"A\" Cell Breeding Device", outputPosition0.name());
        assertEquals(29L, outputPosition0.category());
        assertNull(outputPosition0.type());
        assertNull(outputPosition0.attribute());
        assertNull(outputPosition0.levelRankLink());
        assertTrue(outputPosition0.effectLoreText().startsWith("During each of your Standby Phases"));
        assertNull(outputPosition0.pendulumScale());
        assertNull(outputPosition0.linkArrows());
        assertNull(outputPosition0.atk());
        assertNull(outputPosition0.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals("3-Hump Lacooda", outputPosition9.name());
        assertEquals(1L, outputPosition9.category());
        assertEquals(2L, outputPosition9.type());
        assertEquals("EARTH", outputPosition9.attribute());
        assertEquals(3, outputPosition9.levelRankLink());
        assertTrue(outputPosition9.effectLoreText().startsWith("If there are 3 face-up"));
        assertNull(outputPosition9.pendulumScale());
        assertNull(outputPosition9.linkArrows());
        assertEquals(500, outputPosition9.atk());
        assertEquals(1500, outputPosition9.def());
    }

    @Test
    @Order(201)
    void testFindByIdAsCustomer() {
        YugiohCardDTO output = 
            given()
				.spec(specification)
                    .pathParam("yugioh-card-id", 2061)
				.when()
				    .get("/{yugioh-card-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
                                .as(YugiohCardDTO.class);
        
        assertEquals(2061L, output.id());
        assertEquals("Crystal Beast Sapphire Pegasus", output.name());
        assertEquals(1L, output.category());
        assertEquals(2L, output.type());
        assertEquals("WIND", output.attribute());
        assertEquals(4, output.levelRankLink());
        assertTrue(output.effectLoreText().startsWith("When this card is Summoned: You can place"));
        assertNull(output.pendulumScale());
        assertNull(output.linkArrows());
        assertEquals(1800, output.atk());
        assertEquals(1200, output.def());
    }

    @Test
    @Order(201)
    void testCreateAsCustomer() {
        List<String> linkArrows = Arrays.asList("W", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Onyx Gorilla", 19L, 2L, "EARTH", 
            2, "Effect lore text...", null, 
            linkArrows, 2000, null);

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .body(cardDTO)
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
        assertEquals("/api/v1/product/yugioh-card", output.getInstance());
    }

    @Test
    @Order(201)
    void testUpdateAsCustomer() {
        List<String> linkArrows = Arrays.asList("SW", "S");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            1L, "Crystal Beast Onyx Gorilla", 1L, 1L, "EARTH", 
            2, "Effect lore text", null, 
            linkArrows, 2000, 1000);

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .pathParam("yugioh-card-id", 1)
                    .body(cardDTO)
				.when()
				    .put("/{yugioh-card-id}")
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
        assertEquals("/api/v1/product/yugioh-card/1", output.getInstance());
    }

    @Test
    @Order(201)
    void testDeleteAsCustomer() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .pathParam("yugioh-card-id", 1)
				.when()
				    .delete("/{yugioh-card-id}")
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
        assertEquals("/api/v1/product/yugioh-card/1", output.getInstance());
    }

    @Test
    @Order(201)
    void testFindAllAttributesAsCustomer() {
        List<String> output = 
            given()
                .spec(specification)
				.when()
				    .get("/attribute")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", String.class);

        assertEquals(7, output.size());
        assertEquals("DARK", output.get(0));
        assertEquals("WIND", output.get(6));
    }

    @Test
    @Order(201)
    void testFindAllCategoriesAsCustomer() {
        List<YugiohCardCategory> output = 
            given()
                .spec(specification)
				.when()
				    .get("/category")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", YugiohCardCategory.class);

        assertEquals(36, output.size());

        YugiohCardCategory outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.getId());
        assertEquals("Monster", outputPosition0.getCategory());
        assertEquals("Effect", outputPosition0.getSubCategory());
        assertTrue(outputPosition0.getMainDeck());
        assertNull(outputPosition0.getYugiohCards());

        YugiohCardCategory outputPosition35 = output.get(35);
        assertEquals(36L, outputPosition35.getId());
        assertEquals("Others", outputPosition35.getCategory());
        assertEquals("Token", outputPosition35.getSubCategory());
        assertFalse(outputPosition35.getMainDeck());
        assertNull(outputPosition35.getYugiohCards());
    }

    @Test
    @Order(201)
    void testFindAllTypesAsCustomer() {
        List<YugiohCardType> output = 
            given()
                .spec(specification)
				.when()
				    .get("/type")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
                                    .getList(".", YugiohCardType.class);

        assertEquals(26, output.size());

        YugiohCardType outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.getId());
        assertEquals("Aqua", outputPosition0.getDescription());
        assertNull(outputPosition0.getYugiohCards());

        YugiohCardType outputPosition25 = output.get(25);
        assertEquals(26L, outputPosition25.getId());
        assertEquals("Zombie", outputPosition25.getDescription());
        assertNull(outputPosition25.getYugiohCards());
    }
}
