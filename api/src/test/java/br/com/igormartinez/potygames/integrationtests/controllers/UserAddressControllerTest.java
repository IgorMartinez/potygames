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
import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.data.request.AccountCredentials;
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
public class UserAddressControllerTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;

    private static String ADMIN_EMAIL = "rlayzell0@pen.io";
    private static String ADMIN_PASSWORD = "SDNrJOfLg";

	private static String CUSTOMER_EMAIL = "useraddresscontroler@customer.test";
	private static String CUSTOMER_PASSWORD = "securedpassword";
	private static Long CUSTOMER_ID; // defined in signupAndAuthentication() 
	private static Long CUSTOMER_ADDRESS_ID; // defined in testCreateWithSameUser()

	@Test
    @Order(0)
    void testFindAllAsUnauthenticated() {
        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/user/1/address", output.getInstance());
    }

	@Test
    @Order(0)
    void testFindByIdAsUnauthenticated() {
        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/user/1/address/1", output.getInstance());
    }

	@Test
    @Order(0)
    void testCreateAsUnauthenticated() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null,
            CUSTOMER_ID, 
            Boolean.FALSE, 
            Boolean.FALSE, 
            "Home", 
            "Avenida Bueno Siqueira", 
            "5684", 
            null, 
            "São Afonso", 
            "Santo Antônio", 
            "São Paulo", 
            "Brasil", 
            "10001-555"
		);

        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/user/1/address", output.getInstance());
    }

	@Test
    @Order(0)
    void testUpdateAsUnauthenticated() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			1L,
            CUSTOMER_ID, 
            Boolean.FALSE, 
            Boolean.FALSE, 
            "Home", 
            "Avenida Bueno Siqueira", 
            "5684", 
            null, 
            "São Afonso", 
            "Santo Antônio", 
            "São Paulo", 
            "Brasil", 
            "10001-555"
		);

        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/user/1/address/1", output.getInstance());
    }

	@Test
    @Order(0)
    void testDeleteAsUnauthenticated() {
        ExceptionResponse output = 
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
                                .as(ExceptionResponse.class);

        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Forbidden", output.getTitle());
        assertEquals(HttpStatus.FORBIDDEN.value(), output.getStatus().intValue());
        assertEquals("Authentication required", output.getDetail());
        assertEquals("/api/v1/user/1/address/1", output.getInstance());
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
	@Order(110)
	void testFindAllAsCustomerWithOtherUser() {
		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", CUSTOMER_ID+1)
				.when()
					.get("/{user-id}/address")
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
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address", output.getInstance());
	}

	@Test
	@Order(110)
	void testFindAllAsCustomerWithIdUserInvalid() {
		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
				.when()
					.get("/{user-id}/address")
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
        assertEquals("/api/v1/user/"+0+"/address", output.getInstance());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithSameUser() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null,
            CUSTOMER_ID, 
            Boolean.FALSE, 
            Boolean.FALSE, 
            "Home", 
            "Avenida Bueno Siqueira", 
            "5684", 
            null, 
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
		UserAddressDTO addressDTO = new UserAddressDTO(
			null,
            CUSTOMER_ID+1, 
            Boolean.FALSE, 
            Boolean.FALSE, 
            "Home", 
            "Avenida Bueno Siqueira", 
            "5684", 
            null, 
            "São Afonso", 
            "Santo Antônio", 
            "São Paulo", 
            "Brasil", 
            "10001-555"
		);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address", output.getInstance());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithIdUserInvalid() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+0+"/address", output.getInstance());
	}

	@Test
	@Order(120)
	void testCreateAsCustomerWithMismatchIdUserAndDTO() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, CUSTOMER_ID+1, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address", output.getInstance());
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
	void testUpdateAsCustomerWithSameUser() {
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
	@Order(130)
	void testUpdateAsCustomerWithOtherUser() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			CUSTOMER_ADDRESS_ID,
            CUSTOMER_ID+1, 
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

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithIdUserInvalid() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+0+"/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithIdAddressInvalid() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/0", output.getInstance());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithMismatchIdAddressAndDTO() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			CUSTOMER_ADDRESS_ID+1, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(130)
	void testUpdateAsCustomerWithMismatchIdUserAndDTO() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			CUSTOMER_ADDRESS_ID, CUSTOMER_ID+1, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}
	
	@Test
	@Order(130)
	void testUpdateAsCustomerWithIdAddressWrong() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			CUSTOMER_ADDRESS_ID+1,
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

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.getInstance());
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

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithIdUserInvalid() {

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/0/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithIdAddressInvalid() {

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/0", output.getInstance());
	}

	@Test
	@Order(140)
	void testFindByIdAsCustomerWithIdAddressWrong() {

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.getInstance());
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

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Unauthorized", output.getTitle());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), output.getStatus().intValue());
        assertEquals("The user is not authorized to access this resource.", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID+1)+"/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithIdUserInvalid() {

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/0/address/"+CUSTOMER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithIdAddressInvalid() {

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Bad Request", output.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), output.getStatus().intValue());
        assertEquals("Request object cannot be null", output.getDetail());
        assertEquals("/api/v1/user/"+CUSTOMER_ID+"/address/0", output.getInstance());
	}

	@Test
	@Order(150)
	void testDeleteAsCustomerWithIdAddressWrong() {

		ExceptionResponse output =
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
								.as(ExceptionResponse.class);
		
        assertNotNull(output);
        assertEquals("about:blank", output.getType());
        assertEquals("Not Found", output.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), output.getStatus().intValue());
        assertEquals("The resource was not found", output.getDetail());
        assertEquals("/api/v1/user/"+(CUSTOMER_ID)+"/address/"+(CUSTOMER_ADDRESS_ID+1), output.getInstance());
	}

	@Test
    @Order(200)
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
