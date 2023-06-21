package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
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
public class UserControllerAsAdminTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";
    
    // In Admin, test the permission to edit information of other user
    private static Long CUSTOMER_ID; // defined in signupNewCustomerAndAuthenticationAsAdmin() 
    private static String CUSTOMER_EMAIL = "usercontroller@admin.test";
    private static String CUSTOMER_NAME = "User Controller Test";
    private static LocalDate CUSTOMER_BIRTH_DATE = LocalDate.of(1996,7,23);
    private static String CUSTOMER_DOCUMENT_NUMBER = "023.007.023-00";
    private static String CUSTOMER_PHONE_NUMBER = "+5500987654321";

    @BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

    @Test
    @Order(0)
    void signupNewCustomerAndAuthenticationAsAdmin() {
        UserRegistrationDTO user = 
            new UserRegistrationDTO(
                CUSTOMER_EMAIL, 
                "securedpassword", 
                CUSTOMER_NAME, 
                CUSTOMER_BIRTH_DATE, 
                CUSTOMER_DOCUMENT_NUMBER,
                CUSTOMER_PHONE_NUMBER);

        CUSTOMER_ID = 
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
			.setBasePath("/api/v1/user")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

    @Test
    @Order(1)
    void testFindAll() throws JsonMappingException, JsonProcessingException {
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

        assertNotNull(output);
		assertEquals(11, output.size());

        UserDTO outputPosition0 = output.get(0);
        assertNotNull(output);
        assertEquals(1L, outputPosition0.id());
        assertEquals("rlayzell0@pen.io", outputPosition0.email());
        assertEquals("Rey Layzell", outputPosition0.name());
        assertEquals(LocalDate.of(1966,6,11), outputPosition0.birthDate());
        assertEquals("755.507.583-29", outputPosition0.documentNumber());
        assertEquals("+5545940815815", outputPosition0.phoneNumber());
        assertTrue(outputPosition0.accountNonExpired());
        assertTrue(outputPosition0.accountNonLocked());
        assertTrue(outputPosition0.credentialsNonExpired());
        assertTrue(outputPosition0.enabled());
        assertEquals(1, outputPosition0.permissions().size());
        assertEquals(PermissionType.ADMIN.getValue(), outputPosition0.permissions().get(0));

        UserDTO outputPosition4 = output.get(4);
        assertNotNull(outputPosition4);
        assertEquals(5L, outputPosition4.id());
        assertEquals("vhorbart4@hexun.com", outputPosition4.email());
        assertEquals("Vallie Horbart", outputPosition4.name());
        assertEquals(LocalDate.of(2000,9,4), outputPosition4.birthDate());
        assertEquals("103.467.163-55", outputPosition4.documentNumber());
        assertEquals("+5565901976027", outputPosition4.phoneNumber());
        assertFalse(outputPosition4.accountNonExpired());
        assertFalse(outputPosition4.accountNonLocked());
        assertTrue(outputPosition4.credentialsNonExpired());
        assertFalse(outputPosition4.enabled());
        assertEquals(1, outputPosition4.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), outputPosition4.permissions().get(0));

        UserDTO outputPosition10 = output.get(10);
        assertNotNull(outputPosition10);
        assertEquals(CUSTOMER_ID, outputPosition10.id());
        assertEquals(CUSTOMER_EMAIL, outputPosition10.email());
        assertEquals(CUSTOMER_NAME, outputPosition10.name());
        assertEquals(CUSTOMER_BIRTH_DATE, outputPosition10.birthDate());
        assertEquals(CUSTOMER_DOCUMENT_NUMBER, outputPosition10.documentNumber());
        assertEquals(CUSTOMER_PHONE_NUMBER, outputPosition10.phoneNumber());
        assertTrue(outputPosition10.accountNonExpired());
        assertTrue(outputPosition10.accountNonLocked());
        assertTrue(outputPosition10.credentialsNonExpired());
        assertTrue(outputPosition10.enabled());
        assertEquals(1, outputPosition10.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), outputPosition10.permissions().get(0));
    }

    @Test
    @Order(1)
    void testFindById() {
        UserDTO output = 
            given()
                .spec(specification)
                    .pathParam("id", CUSTOMER_ID)
                .when()
                    .get("{id}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserDTO.class);
        
        assertNotNull(output);
        assertEquals(CUSTOMER_ID, output.id());
        assertEquals(CUSTOMER_EMAIL, output.email());
        assertEquals(CUSTOMER_NAME, output.name());
        assertEquals(CUSTOMER_BIRTH_DATE, output.birthDate());
        assertEquals(CUSTOMER_DOCUMENT_NUMBER, output.documentNumber());
        assertEquals(CUSTOMER_PHONE_NUMBER, output.phoneNumber());
        assertTrue(output.accountNonExpired());
        assertTrue(output.accountNonLocked());
        assertTrue(output.credentialsNonExpired());
        assertTrue(output.enabled());
        assertEquals(1, output.permissions().size());
        assertEquals(PermissionType.CUSTOMER.getValue(), output.permissions().get(0));
    }

    @Test
    @Order(2)
    void testUpdatePersonalInformation() {
        UserPersonalInformationDTO updateCustomer = 
            new UserPersonalInformationDTO(
                CUSTOMER_ID, 
                "Test Name Updated", 
                LocalDate.of(2023,06,14), 
                "000.000.000-00",
                "+5511999991111");

        UserPersonalInformationDTO output = 
            given()
                .spec(specification)
                    .pathParam("id", CUSTOMER_ID)
                    .body(updateCustomer)
                .when()
                    .put("{id}/personal-information")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .body()
                        .as(UserPersonalInformationDTO.class);
        
        assertNotNull(output);
        assertEquals(CUSTOMER_ID, output.id());
        assertEquals("Test Name Updated", output.name());
        assertEquals(LocalDate.of(2023,06,14), output.birthDate());
        assertEquals("000.000.000-00", output.documentNumber());
        assertEquals("+5511999991111", output.phoneNumber());
    }

    @Test
    @Order(4)
    void testDelete() {
        given()
			.spec(specification)
				.pathParam("id", CUSTOMER_ID)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
			.extract()
				.body()
					.asString();
    }
}
