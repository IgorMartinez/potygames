package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

	private static Long USER_ID; // defined in testSignup() 
    private static String USER_EMAIL = "usercontroller@customer.test";
	private static String USER_PASSWORD = "securedpassword";
    private static String USER_NAME = "User Controller Test";
    private static LocalDate USER_BIRTH_DATE = LocalDate.of(1996,7,23);
    private static String USER_DOCUMENT_NUMBER = "023.007.023-00";
    private static String USER_PHONE_NUMBER = "+5500987654321";

    @Test
    @Order(0)
    void testSignup() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                USER_EMAIL, 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        UserDTO customerDTO = 
            given()
                .basePath("/api/v1/user/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .body()
                                .as(UserDTO.class);
        
        assertTrue(customerDTO.id() > 0);
        assertEquals(USER_EMAIL, customerDTO.email());
        assertEquals(USER_NAME, customerDTO.name());
        assertEquals(USER_BIRTH_DATE, customerDTO.birthDate());
        assertEquals(USER_DOCUMENT_NUMBER, customerDTO.documentNumber());
        assertTrue(customerDTO.accountNonExpired());
        assertTrue(customerDTO.accountNonLocked());
        assertTrue(customerDTO.credentialsNonExpired());
        assertTrue(customerDTO.enabled());
        assertEquals(1, customerDTO.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), customerDTO.permissions().get(0));

        USER_ID = customerDTO.id();
    }

    @Test
    @Order(0)
    void testSignupWithoutBody() {
        APIErrorResponse output = 
            given()
                .basePath("/api/v1/user/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
        assertEquals("/api/v1/user/signup", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testSignupWithEmailBlank() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "", 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        APIErrorResponse output = 
            given()
                .basePath("/api/v1/user/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
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
        assertEquals("/api/v1/user/signup", output.instance());
        assertEquals(1, output.errors().size());
        assertEquals("The email must be not blank.", output.errors().get("email"));
    }

    @Test
    @Order(0)
    void testSignupWithUserAlreadyExists() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                "rlayzell0@pen.io", 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        APIErrorResponse output = 
            given()
                .basePath("/api/v1/user/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                        .extract()
                            .body()
                                .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Conflict", output.title());
        assertEquals(HttpStatus.CONFLICT.value(), output.status());
        assertEquals("The email is already in use.", output.detail());
        assertEquals("/api/v1/user/signup", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testFindAllAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				    .get()
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
				.when()
				    .get("/{user-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
				.when()
				    .delete("/{user-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(0)
    void testUpdatePersonalInformationAsUnauthenticated() {
        UserPersonalInformationDTO userPersonalInformationDTO = 
            new UserPersonalInformationDTO(
                1L, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
                    .body(userPersonalInformationDTO)
				.when()
				    .put("/{user-id}/personal-information")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/personal-information", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(100)
    void authenticationAsCustomer() {
        AccountCredentials accountCredentials = new AccountCredentials(USER_EMAIL, USER_PASSWORD);

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
			.setBasePath("/api/v1/user")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(110)
    void testFindAllAsCustomer() {
        APIErrorResponse output =
            given()
                .spec(specification)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testFindByIdAsCustomerWithSameUser() {
        UserDTO output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", USER_ID)
                .when()
                    .get("{user-id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertEquals(USER_ID, output.id());
        assertEquals(USER_EMAIL, output.email());
        assertEquals(USER_NAME, output.name());
        assertEquals(USER_BIRTH_DATE, output.birthDate());
        assertEquals(USER_DOCUMENT_NUMBER, output.documentNumber());
        assertEquals(USER_PHONE_NUMBER, output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));
    }

    @Test
    @Order(110)
    void testFindByIdAsCustomerWithOtherUser() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", USER_ID+1)
                .when()
                    .get("{user-id}")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(USER_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(110)
    void testFindByIdAsCustomerWithIdInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", 0)
                .when()
                    .get("{user-id}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/0", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdatePersonalInformationAsCustomerWithBodyEmpty() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/user/"+USER_ID+"/personal-information", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdatePersonalInformationAsCustomerWithFieldsNull() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                null, 
                "  ", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID)
                    .body(updateCustomer)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/user/"+USER_ID+"/personal-information", output.instance());
        assertEquals(2, output.errors().size());
        assertEquals("The id of user must be provided.", output.errors().get("id"));
        assertEquals("The name must be not blank.", output.errors().get("name"));
    }

    @Test
    @Order(120)
    void testUpdatePersonalInformationAsCustomerWithIdInvalid() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                Long.valueOf(3), 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", 0)
                    .body(updateCustomer)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/0/personal-information", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdatePersonalInformationAsCustomerWithMismatchDTOIdAndParamId() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                USER_ID, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID+1)
                    .body(updateCustomer)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the user-id parameter.", output.detail());
        assertEquals("/api/v1/user/"+(USER_ID+1)+"/personal-information", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(120)
    void testUpdatePersonalInformationAsCustomerWithSameUser() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                USER_ID, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        UserDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID)
                    .body(updateCustomer)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertEquals(USER_ID, output.id());
        assertEquals("Test Name Updated", output.name());
        assertEquals(LocalDate.of(2023,06,14), output.birthDate());
        assertEquals("000.000.000-00", output.documentNumber());
        assertEquals("+5511999990000", output.phoneNumber());
    }

    @Test
    @Order(120)
    void testUpdatePersonalInformationAsCustomerWithOtherUser() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                USER_ID+1, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999990000");

        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID+1)
                    .body(updateCustomer)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(USER_ID+1)+"/personal-information", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsCustomerWithIdInvalid() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", -10)
                .when()
                    .delete("{user-id}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/-10", output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(130)
    void testDeleteAsCustomerWithOtherUser() {
        APIErrorResponse output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID+1)
                .when()
                    .delete("{user-id}")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(APIErrorResponse.class);
        
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(USER_ID+1), output.instance());
        assertNull(output.errors());
    }

    @Test
    @Order(131)
    void testDeleteAsCustomerWithSameUser() {
        given()
			.spec(specification)
				.pathParam("user-id", USER_ID)
			.when()
				.delete("{user-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
			.extract()
				.body()
					.asString();
    }

    @Test
    @Order(200)
    void authenticationAsAdminAndSignupNewCustomer() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                USER_EMAIL, 
                USER_PASSWORD, 
                USER_NAME, 
                USER_BIRTH_DATE, 
                USER_DOCUMENT_NUMBER,
                USER_PHONE_NUMBER);

        USER_ID = 
            given()
                .basePath("/api/v1/user/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract()
                            .body()
                                .as(UserDTO.class)
									.id();

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
			.setBasePath("/api/v1/user")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.build();
    }

    @Test
    @Order(210)
    void testFindAllAsAdmin() {
        List<UserDTO> output =
            given()
                .spec(specification)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .jsonPath()
                            .getList(".", UserDTO.class);

		assertEquals(1002, output.size());

        UserDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("admin@potygames.com", outputPosition0.email());
        assertEquals("Admin", outputPosition0.name());
        assertEquals(LocalDate.of(1966,6,11), outputPosition0.birthDate());
        assertEquals("000.000.000-00", outputPosition0.documentNumber());
        assertEquals("+5500000000000", outputPosition0.phoneNumber());
        assertTrue(outputPosition0.accountNonExpired());
        assertTrue(outputPosition0.accountNonLocked());
        assertTrue(outputPosition0.credentialsNonExpired());
        assertTrue(outputPosition0.enabled());
        assertEquals(2, outputPosition0.permissions().size());
        assertTrue(List.of(PermissionType.ADMIN.getValue(), PermissionType.CUSTOMER.getValue())
            .containsAll(outputPosition0.permissions()));

        UserDTO outputPosition1002 = output.get(1002);
        assertEquals(USER_ID, outputPosition1002.id());
        assertEquals(USER_EMAIL, outputPosition1002.email());
        assertEquals(USER_NAME, outputPosition1002.name());
        assertEquals(USER_BIRTH_DATE, outputPosition1002.birthDate());
        assertEquals(USER_DOCUMENT_NUMBER, outputPosition1002.documentNumber());
        assertEquals(USER_PHONE_NUMBER, outputPosition1002.phoneNumber());
        assertTrue(outputPosition1002.accountNonExpired());
        assertTrue(outputPosition1002.accountNonLocked());
        assertTrue(outputPosition1002.credentialsNonExpired());
        assertTrue(outputPosition1002.enabled());
        assertEquals(1, outputPosition1002.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), outputPosition1002.permissions().get(0));
    }

    @Test
    @Order(220)
    void testFindByIdAsAdmin() {
        UserDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID)
                .when()
                    .get("{user-id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertEquals(USER_ID, output.id());
        assertEquals(USER_EMAIL, output.email());
        assertEquals(USER_NAME, output.name());
        assertEquals(USER_BIRTH_DATE, output.birthDate());
        assertEquals(USER_DOCUMENT_NUMBER, output.documentNumber());
        assertEquals(USER_PHONE_NUMBER, output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));
    }

    @Test
    @Order(230)
    void testUpdatePersonalInformationAsAdmin() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                USER_ID, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999991111");

        UserDTO output = 
            given()
                .spec(specification)
                    .pathParam("user-id", USER_ID)
                    .body(updateCustomer)
                .when()
                    .put("{user-id}/personal-information")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertEquals(USER_ID, output.id());
        assertEquals("Test Name Updated", output.name());
        assertEquals(LocalDate.of(2023,06,14), output.birthDate());
        assertEquals("000.000.000-00", output.documentNumber());
        assertEquals("+5511999991111", output.phoneNumber());
    }

    @Test
    @Order(240)
    void testDeleteAsAdmin() {
        given()
			.spec(specification)
				.pathParam("user-id", USER_ID)
			.when()
				.delete("{user-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
			.extract()
				.body()
					.asString();
    }

}
