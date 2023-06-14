package br.com.igormartinez.potygames.integrationtests.controllers.usercontroller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.exceptions.ExceptionResponse;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerAsCustomerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    @Test
    @Order(0)
    void authentication() {

        AccountCredentials accountCredentials = 
            new AccountCredentials(
                TestConfigs.USER_CUSTOMER_USERNAME, 
                TestConfigs.USER_CUSTOMER_PASSWORD);

        String accessToken = 
			given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(accountCredentials)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(Token.class)
								    .getAccessToken();

		specification = new RequestSpecBuilder()
			.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
			.setBasePath("/api/user/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(1)
    void testFindAll() {
        ExceptionResponse exceptionResponse =
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);

        assertNotNull(exceptionResponse);
        assertEquals("about:blank", exceptionResponse.getType());
        assertEquals("Unauthorized", exceptionResponse.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exceptionResponse.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource", exceptionResponse.getDetail());
        assertEquals("/api/user/v1", exceptionResponse.getInstance());
    }

    @Test
    @Order(1)
    void testFindByIdWithSameUser() {
        UserDTO output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 2)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertTrue(output.id() > 0);
        assertEquals(2, output.id());
        assertEquals("fragge1@blinklist.com", output.email());
        assertEquals("Fayre Ragge", output.name());
        assertEquals(LocalDate.of(1984,04,24), output.birthDate());
        assertEquals("917.590.242-42", output.documentNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));
    }

    @Test
    @Order(1)
    void testFindByIdWithOtherUser() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 3)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/3", output.getInstance());
    }

    @Test
    @Order(1)
    void testFindByIdWithIdInvalid() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 0)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/user/v1/0", output.getInstance());
    }

    @Test
    @Order(2)
    void testUpdatePersonalInformationWithSameUser() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                Long.valueOf(2L), 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00");

        UserPersonalInformationDTO output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 2)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserPersonalInformationDTO.class);
        
        assertNotNull(output);
        assertEquals(2, output.id());
        assertEquals("Test Name Updated", output.name());
        assertEquals(LocalDate.of(2023,06,14), output.birthDate());
        assertEquals("000.000.000-00", output.documentNumber());
    }

    @Test
    @Order(2)
    void testUpdatePersonalInformationWithOtherUser() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                Long.valueOf(3), 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00");

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 3)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/3/personal-information", output.getInstance());
    }

    @Test
    @Order(2)
    void testUpdatePersonalInformationWithIdInvalid() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                Long.valueOf(3), 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00");

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 555)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/user/v1/555/personal-information", output.getInstance());
    }

    @Test
    @Order(2)
    void testUpdatePersonalInformationWithBodyEmpty() {

        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 555)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Failed to read request", output.getDetail());
        assertEquals("/api/user/v1/555/personal-information", output.getInstance());
    }

    @Test
    @Order(3)
    void testDeleteWithOtherUser() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", 3)
                .when()
                    .delete("{id}")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus());
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/3", output.getInstance());
    }

    @Test
    @Order(3)
    void testDeleteWithIdInvalid() {
        ExceptionResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("id", -10)
                .when()
                    .delete("{id}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);
        
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/user/v1/-10", output.getInstance());
    }

    @Test
    @Order(4)
    void testDeleteWithSameUser() {
        given()
			.spec(specification)
			    .contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", 2)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
			.extract()
				.body()
					.asString();
    }
}
