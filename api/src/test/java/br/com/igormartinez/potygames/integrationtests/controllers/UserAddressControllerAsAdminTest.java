package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
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
import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.security.v1.AccountCredentials;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserAddressControllerAsAdminTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";
    
    // In Admin, test the permission to edit information of other user
    private static Long CUSTOMER_ID; // defined in signupNewCustomerAndAuthenticationAsAdmin() 
    private static String CUSTOMER_EMAIL = "useraddresscontroller@admin.test";
    private static String CUSTOMER_NAME = "User Address Controller Test";
    private static LocalDate CUSTOMER_BIRTH_DATE = LocalDate.of(1996,7,23);
    private static String CUSTOMER_DOCUMENT_NUMBER = "023.007.023-00";
	private static String CUSTOMER_PHONE_NUMBER = "+5500987654321";
    private static Long CUSTOMER_ADDRESS_ID; // defined in testCreate

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
	@Order(10)
	void testCreate() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null,
            CUSTOMER_ID, 
            Boolean.FALSE, 
            Boolean.FALSE, 
            "Home", 
            "Avenida Bueno Siqueira", 
            "5684", 
            "House", 
            "São Afonso", 
            "Santo Antônio", 
            "São Paulo", 
            "Brasil", 
            "10001-555"
		);

		UserAddressDTO output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.body(addressDTO)
				.when()
					.post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.as(UserAddressDTO.class);
		
		assertNotNull(output);
		assertTrue(output.id() > 0);
		assertEquals(CUSTOMER_ID, output.idUser());
		assertFalse(output.favorite());
		assertFalse(output.billingAddress());
        assertEquals("Home", output.description());
        assertEquals("Avenida Bueno Siqueira", output.street());
        assertEquals("5684", output.number());
		assertEquals("House", output.complement());
        assertEquals("São Afonso", output.neighborhood());
        assertEquals("Santo Antônio", output.city());
        assertEquals("São Paulo", output.state());
        assertEquals("Brasil", output.country());
        assertEquals("10001-555", output.zipCode());

		CUSTOMER_ADDRESS_ID = output.id();
	}

    @Test
	@Order(20)
	void testFindAll() {
		List<UserAddressDTO> output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
				.when()
					.get("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.jsonPath()
									.getList(".", UserAddressDTO.class);
		assertNotNull(output);
		assertEquals(1, output.size());

		UserAddressDTO output0 = output.get(0);
		assertNotNull(output0);
		assertEquals(CUSTOMER_ADDRESS_ID, output0.id());
		assertEquals(CUSTOMER_ID, output0.idUser());
		assertFalse(output0.favorite());
		assertFalse(output0.billingAddress());
        assertEquals("Home", output0.description());
        assertEquals("Avenida Bueno Siqueira", output0.street());
        assertEquals("5684", output0.number());
        assertEquals("House", output0.complement());
        assertEquals("São Afonso", output0.neighborhood());
        assertEquals("Santo Antônio", output0.city());
        assertEquals("São Paulo", output0.state());
        assertEquals("Brasil", output0.country());
        assertEquals("10001-555", output0.zipCode());
	}

    @Test
	@Order(30)
	void testUpdate() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			CUSTOMER_ADDRESS_ID,
            CUSTOMER_ID, 
            Boolean.TRUE, 
            Boolean.FALSE, 
            "Home", 
            "Avenida João", 
            "5684", 
            null, 
            "São Afonso", 
            "São Francisco", 
            "São Paulo", 
            "Brasil", 
            "10101-555"
		);

		UserAddressDTO output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.as(UserAddressDTO.class);
		
		assertNotNull(output);
		assertEquals(CUSTOMER_ADDRESS_ID, output.id());
		assertEquals(CUSTOMER_ID, output.idUser());
		assertTrue(output.favorite());
		assertFalse(output.billingAddress());
        assertEquals("Home", output.description());
        assertEquals("Avenida João", output.street());
        assertEquals("5684", output.number());
		assertNull(output.complement());
        assertEquals("São Afonso", output.neighborhood());
        assertEquals("São Francisco", output.city());
        assertEquals("São Paulo", output.state());
        assertEquals("Brasil", output.country());
        assertEquals("10101-555", output.zipCode());
	}

    @Test
	@Order(40)
	void testDelete() {
		given()
			.spec(specification)
				.pathParam("user-id", CUSTOMER_ID)
				.pathParam("address-id", CUSTOMER_ADDRESS_ID)
			.when()
				.delete("/{user-id}/address/{address-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
					.extract()
						.body()
							.asString();
	}

    @Test
	@Order(1000)
	void removeUserTest() {
		given()
			.spec(specification)
			    .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
