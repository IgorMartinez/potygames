package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
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
    private static UserDTO customerDTO;

    private static String NEW_CUSTOMER_EMAIL = "signupnewcustomer@test.com";
    private static String NEW_CUSTOMER_PASSWORD = "testsignup";
    private static String NEW_CUSTOMER_NAME = "Signup Test";
    private static LocalDate NEW_CUSTOMER_BIRTH_DATE = LocalDate.of(2023,6,13);
    private static String NEW_CUSTOMER_DOCUMENT_NUMBER = "023.006.013-00";

    @Test
    @Order(0)
    void testSignupNewCustomer() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                NEW_CUSTOMER_EMAIL, 
                NEW_CUSTOMER_PASSWORD, 
                NEW_CUSTOMER_NAME, 
                NEW_CUSTOMER_BIRTH_DATE, 
                NEW_CUSTOMER_DOCUMENT_NUMBER);

        customerDTO = 
            given()
                .basePath("/api/user/v1/signup")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                    .post()
                .then()
                    .statusCode(200)
                        .extract()
                            .body()
                                .as(UserDTO.class);
        
        assertNotNull(customerDTO);
        assertNotNull(customerDTO.id());
        assertTrue(customerDTO.id() > 0);
        assertNotNull(customerDTO.email());
        assertEquals(NEW_CUSTOMER_EMAIL, customerDTO.email());
        assertNotNull(customerDTO.name());
        assertEquals(NEW_CUSTOMER_NAME, customerDTO.name());
        assertNotNull(customerDTO.birthDate());
        assertEquals(NEW_CUSTOMER_BIRTH_DATE, customerDTO.birthDate());
        assertNotNull(customerDTO.documentNumber());
        assertEquals(NEW_CUSTOMER_DOCUMENT_NUMBER, customerDTO.documentNumber());
        assertNotNull(customerDTO.accountNonExpired());
        assertTrue(customerDTO.accountNonExpired());
        assertNotNull(customerDTO.accountNonLocked());
        assertTrue(customerDTO.accountNonLocked());
        assertNotNull(customerDTO.credentialsNonExpired());
        assertTrue(customerDTO.credentialsNonExpired());
        assertNotNull(customerDTO.enabled());
        assertTrue(customerDTO.enabled());
        assertNotNull(customerDTO.permissions());
        assertEquals(1, customerDTO.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), customerDTO.permissions().get(0));
    }

    @Test
    @Order(1)
    void testAuthenticationWithNewCustomer() {
        AccountCredentials accountCredentials = 
            new AccountCredentials(NEW_CUSTOMER_EMAIL, NEW_CUSTOMER_PASSWORD);

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
    @Order(2)
    void testFindAllAsCustomer() {
        ExceptionResponse exceptionResponse =
            given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                    .body()
                        .as(ExceptionResponse.class);

        assertNotNull(exceptionResponse);
        assertNotNull(exceptionResponse.getTimestamp());
        assertNotNull(exceptionResponse.getMessage());
        assertEquals("The user not have permission to this resource", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getDetails());
        assertEquals("uri=/api/user/v1", exceptionResponse.getDetails());
    }

    @Test
    @Order(3)
    void testFindByIdAsCustomer() {
        UserDTO output = 
            given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                    .pathParam("id", customerDTO.id())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertNotNull(output.id());
        assertTrue(output.id() > 0);
        assertEquals(customerDTO.id(), output.id());
        assertNotNull(output.email());
        assertEquals(NEW_CUSTOMER_EMAIL, output.email());
        assertNotNull(output.name());
        assertEquals(NEW_CUSTOMER_NAME, output.name());
        assertNotNull(output.birthDate());
        assertEquals(NEW_CUSTOMER_BIRTH_DATE, output.birthDate());
        assertNotNull(output.documentNumber());
        assertEquals(NEW_CUSTOMER_DOCUMENT_NUMBER, output.documentNumber());
        assertNotNull(output.accountNonExpired());
        assertTrue(output.accountNonExpired());
        assertNotNull(output.accountNonLocked());
        assertTrue(output.accountNonLocked());
        assertNotNull(output.credentialsNonExpired());
        assertTrue(output.credentialsNonExpired());
        assertNotNull(output.enabled());
        assertTrue(output.enabled());
        assertNotNull(output.permissions());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));

        customerDTO = output;
    }

    @Test
    @Order(4)
    void testUpdatePersonalInformationAsCustomer() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                customerDTO.id(), 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00");

        UserPersonalInformationDTO output = 
            given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                    .pathParam("id", customerDTO.id())
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserPersonalInformationDTO.class);
        
        assertNotNull(output);
        assertNotNull(output.id());
        assertTrue(output.id() > 0);
        assertEquals(customerDTO.id(), output.id());
        assertNotNull(output.name());
        assertEquals("Test Name Updated", output.name());
        assertNotNull(output.birthDate());
        assertEquals(LocalDate.of(2023,06,14), output.birthDate());
        assertNotNull(output.documentNumber());
        assertEquals("000.000.000-00", output.documentNumber());
    }

    @Test
    @Order(5)
    void testDeleteAsCustomer() {
        given()
			.spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.pathParam("id", customerDTO.id())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204)
			.extract()
				.body()
					.asString();
    }

}
