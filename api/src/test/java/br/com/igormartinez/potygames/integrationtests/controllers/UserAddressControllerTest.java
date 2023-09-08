package br.com.igormartinez.potygames.integrationtests.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
import br.com.igormartinez.potygames.data.request.UserAddressCreateDTO;
import br.com.igormartinez.potygames.data.request.UserAddressUpdateDTO;
import br.com.igormartinez.potygames.data.request.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.response.APIErrorResponse;
import br.com.igormartinez.potygames.data.response.UserAddressDTO;
import br.com.igormartinez.potygames.data.response.UserDTO;
import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserAddressControllerTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;

	private static String CUSTOMER_EMAIL = "useraddresscontroler@customer.test";
	private static String CUSTOMER_PASSWORD = "securedpassword";
	private static Long CUSTOMER_ID; // defined in signupAndAuthentication() 
	private static Long CUSTOMER_ADDRESS_ID; // defined in testCreateWithSameUser()

	@Test
    @Order(0)
    void testFindAllAsUnauthenticated() {
        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
				.when()
				    .get("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/address", output.instance());
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
                    .pathParam("address-id", 1)
				.when()
				    .get("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/address/1", output.instance());
		assertNull(output.errors());
    }

	@Test
    @Order(0)
    void testCreateAsUnauthenticated() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            1L, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
		);

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
					.body(addressDTO)
				.when()
				    .post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/address", output.instance());
		assertNull(output.errors());
    }

	@Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            1L, 1L, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
		);

        APIErrorResponse output = 
            given()
				.basePath("/api/v1/user")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .pathParam("user-id", 1)
					.pathParam("address-id", 1)
					.body(addressDTO)
				.when()
				    .put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/address/1", output.instance());
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
					.pathParam("address-id", 1)
				.when()
				    .delete("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.FORBIDDEN.value())
						.extract()
							.body()
                                .as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Forbidden", output.title());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.status());
        assertEquals("Authentication required", output.detail());
        assertEquals("/api/v1/user/1/address/1", output.instance());
		assertNull(output.errors());
    }

    @Test
    @Order(100)
    void signupAndAuthenticationAsCustomer() {
		UserRegistrationDTO user = 
            new UserRegistrationDTO(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD, 
                "Signup Test", 
                LocalDate.of(1996,7,23), 
                "023.007.023-00",
				"+5500987654321");

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

        AccountCredentials accountCredentials = 
            new AccountCredentials(
                CUSTOMER_EMAIL, 
                CUSTOMER_PASSWORD);

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
	void testFindAllAsCustomerWithIdUserInvalid() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
				.when()
					.get("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/"+0+"/address", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(110)
	void testFindAllAsCustomerWithOtherUser() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
				.when()
					.get("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);

        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(110)
	void testFindAllAsCustomerWithSameUserAndNoAddress() {
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
		assertEquals(0, output.size());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithoutBody() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
				.when()
					.post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithFieldsNull() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            null, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.body(addressDTO)
				.when()
					.post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address", output.instance());
        assertEquals(1, output.errors().size());
        assertEquals("The id of user must be provided.", output.errors().get("idUser"));
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithParamIdUserInvalid() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            1L, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.body(addressDTO)
				.when()
					.post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/"+0+"/address", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithMismatchIdUserAndDTO() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            CUSTOMER_ID+1, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.body(addressDTO)
				.when()
					.post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the user-id parameter.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address", output.instance());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithSameUser() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            CUSTOMER_ID, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
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
		
		assertTrue(output.id() > 0);
		assertEquals(CUSTOMER_ID, output.idUser());
		assertFalse(output.favorite());
		assertFalse(output.billingAddress());
        assertEquals("Home", output.description());
        assertEquals("Avenida Bueno Siqueira", output.street());
        assertEquals("5684", output.number());
		assertNull(output.complement());
        assertEquals("São Afonso", output.neighborhood());
        assertEquals("Santo Antônio", output.city());
        assertEquals("São Paulo", output.state());
        assertEquals("Brasil", output.country());
        assertEquals("10001-555", output.zipCode());

		CUSTOMER_ADDRESS_ID = output.id();
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithOtherUser() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            CUSTOMER_ID+1, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            null, "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
					.body(addressDTO)
				.when()
					.post("/{user-id}/address")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(121)
	void testFindAllAsCustomerWithSameUserAsAddress() {
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
		assertNull(output0.complement());
        assertEquals("São Afonso", output0.neighborhood());
        assertEquals("Santo Antônio", output0.city());
        assertEquals("São Paulo", output0.state());
        assertEquals("Brasil", output0.country());
        assertEquals("10001-555", output0.zipCode());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithoutBody() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Failed to read request", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID), output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithFieldsNull() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            null, 0L, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("Invalid request content.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID), output.instance());
        assertEquals(2, output.errors().size());
        assertEquals("The id of user address must be provided.", output.errors().get("id"));
        assertEquals("The id of user must be a positive number.", output.errors().get("idUser"));
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithParamIdUserInvalid() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/"+0+"/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithParamIdAddressInvalid() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", 0)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The address-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/0", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithMismatchIdUserAndDTO() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID of user in the request body must match the value of the user-id parameter.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithMismatchIdAddressAndDTO() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID+1)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The ID in the request body must match the value of the address-id parameter.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithSameUser() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
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
	@Order(130)
	void testUpdateAsCustomerWithOtherUser() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID+1, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}
	
	@Test
	@Order(130)
	void testUpdateAsCustomerWithAddressNotFound() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID+1, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
		);

		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID+1)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The address was not found with the given ID.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithParamIdUserInvalid() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.get("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/0/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithParamIdAddressInvalid() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", 0)
				.when()
					.get("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The address-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/0", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithSameUser() {
		UserAddressDTO output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.get("/{user-id}/address/{address-id}")
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
	@Order(140)
	void testFindByIdAsCustomerWithOtherUser() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.get("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithAddressNotFound() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID+1)
				.when()
					.get("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The address was not found with the given ID.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithIdUserInvalid() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.delete("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The user-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/0/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithParamIdAddressInvalid() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", 0)
				.when()
					.delete("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.BAD_REQUEST.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Bad Request", output.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.status());
        assertEquals("The address-id must be a positive integer value.", output.detail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/0", output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithSameUser() {
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
	@Order(150)
	void testDeleteAsCustomerWithOtherUser() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.delete("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.UNAUTHORIZED.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Unauthorized", output.title());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.status());
        assertEquals("The user is not authorized to access this resource.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.instance());
		assertNull(output.errors());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithAddressNotFound() {
		APIErrorResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID+1)
				.when()
					.delete("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.NOT_FOUND.value())
						.extract()
							.body()
								.as(APIErrorResponse.class);
		
        assertEquals("about:blank", output.type());
        assertEquals("Not Found", output.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.status());
        assertEquals("The address was not found with the given ID.", output.detail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.instance());
		assertNull(output.errors());
	}

	@Test
    @Order(200)
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
			.setBasePath("/api/v1/user")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

	@Test
	@Order(210)
	void testCreateAsAdmin() {
		UserAddressCreateDTO addressDTO = new UserAddressCreateDTO(
            CUSTOMER_ID, Boolean.FALSE, Boolean.FALSE, 
            "Home", "Avenida Bueno Siqueira", "5684", 
            "House", "São Afonso", "Santo Antônio", 
            "São Paulo", "Brasil", "10001-555"
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
	@Order(220)
	void testFindAllAsAdmin() {
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
	@Order(220)
	void testFindByIdAsAdmin() {
		UserAddressDTO output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID)
					.pathParam("address-id", CUSTOMER_ADDRESS_ID)
				.when()
					.get("/{user-id}/address/{address-id}")
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
	}

	@Test
	@Order(240)
	void testUpdateAsAdmin() {
		UserAddressUpdateDTO addressDTO = new UserAddressUpdateDTO(
            CUSTOMER_ADDRESS_ID, CUSTOMER_ID, Boolean.TRUE, Boolean.FALSE, 
            "Home", "Avenida João", "5684", 
            null, "São Afonso", "São Francisco", 
            "São Paulo", "Brasil", "10101-555"
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
	@Order(250)
	void testDeleteAsAdmin() {
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
