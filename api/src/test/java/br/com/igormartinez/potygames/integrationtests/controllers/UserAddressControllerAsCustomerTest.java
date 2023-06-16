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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.igormartinez.potygames.configs.TestConfigs;
import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
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
public class UserAddressControllerAsCustomerTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;
	private static ObjectMapper objectMapper;

	private static String USER_EMAIL = "useraddresscontroler@customer.test";
	private static String USER_PASSWORD = "securedpassword";
	private static Long USER_ID; // defined in signupAndAuthentication() 
	private static Long USER_ADDRESS_ID; // defined in testCreateWithSameUser()


	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

    @Test
    @Order(0)
    void signupAndAuthentication() {
		UserRegistrationDTO user = 
            new UserRegistrationDTO(
                USER_EMAIL, 
                USER_PASSWORD, 
                "Signup Test", 
                LocalDate.of(1996,7,23), 
                "023.007.023-00");

        USER_ID = 
            given()
                .basePath("/api/user/v1/signup")
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
                USER_EMAIL, 
                USER_PASSWORD);

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
			.setBasePath("/api/user/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
    }

	@Test
	@Order(1)
	void testFindAllAsSameUserWithNoAddress() throws JsonMappingException, JsonProcessingException {
		List<UserAddressDTO> output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
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
	@Order(1)
	void testFindAllAsOtherUser() {
		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID+1)
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
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/"+(USER_ID+1)+"/address", output.getInstance());
	}

	@Test
	@Order(1)
	void testFindAllWithIdUserInvalid() {
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
        assertEquals("/api/user/v1/"+0+"/address", output.getInstance());
	}

	@Test
	@Order(10)
	void testCreateWithSameUser() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null,
            USER_ID, 
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
					.pathParam("user-id", USER_ID)
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
		assertEquals(USER_ID, output.idUser());
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

		USER_ADDRESS_ID = output.id();
	}

	@Test
	@Order(10)
	void testCreateWithOtherUser() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null,
            USER_ID+1, 
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
					.pathParam("user-id", USER_ID+1)
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
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/"+(USER_ID+1)+"/address", output.getInstance());
	}

	@Test
	@Order(10)
	void testCreateWithIdUserInvalid() {
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
        assertEquals("/api/user/v1/"+0+"/address", output.getInstance());
	}

	@Test
	@Order(10)
	void testCreateWithMismatchIdUserAndDTO() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, USER_ID+1, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
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
        assertEquals("/api/user/v1/"+USER_ID+"/address", output.getInstance());
	}

	@Test
	@Order(11)
	void testFindAllAsSameUserWithAddress() throws JsonMappingException, JsonProcessingException {
		List<UserAddressDTO> output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
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
		assertEquals(USER_ADDRESS_ID, output0.id());
		assertEquals(USER_ID, output0.idUser());
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
	@Order(20)
	void testUpdateAsSameUser() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			USER_ADDRESS_ID,
            USER_ID, 
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
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID)
					.body(addressDTO)
				.when()
					.put("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.as(UserAddressDTO.class);
		
		assertNotNull(output);
		assertEquals(USER_ADDRESS_ID, output.id());
		assertEquals(USER_ID, output.idUser());
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
	@Order(20)
	void testUpdateAsOtherUser() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			USER_ADDRESS_ID,
            USER_ID+1, 
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
					.pathParam("user-id", USER_ID+1)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/"+(USER_ID+1)+"/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(20)
	void testUpdateWithIdUserInvalid() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("/api/user/v1/"+0+"/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(20)
	void testUpdateWithIdAddressInvalid() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			null, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
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
        assertEquals("/api/user/v1/"+USER_ID+"/address/0", output.getInstance());
	}

	@Test
	@Order(20)
	void testUpdateWithMismatchIdAddressAndDTO() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			USER_ADDRESS_ID+1, null, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("/api/user/v1/"+USER_ID+"/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(20)
	void testUpdateWithMismatchIdUserAndDTO() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			USER_ADDRESS_ID, USER_ID+1, null, null, 
			null, null, null, null, null, 
			null, null, null, null);

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("/api/user/v1/"+USER_ID+"/address/"+USER_ADDRESS_ID, output.getInstance());
	}
	
	@Test
	@Order(20)
	void testUpdateWithIdAddressWrong() {
		UserAddressDTO addressDTO = new UserAddressDTO(
			USER_ADDRESS_ID+1,
            USER_ID, 
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
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID+1)
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
        assertEquals("/api/user/v1/"+(USER_ID)+"/address/"+(USER_ADDRESS_ID+1), output.getInstance());
	}

	@Test
	@Order(30)
	void testFindByIdAsSameUser() {

		UserAddressDTO output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID)
				.when()
					.get("/{user-id}/address/{address-id}")
				.then()
					.statusCode(HttpStatus.OK.value())
						.extract()
							.body()
								.as(UserAddressDTO.class);
		
		assertNotNull(output);
		assertEquals(USER_ADDRESS_ID, output.id());
		assertEquals(USER_ID, output.idUser());
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
	@Order(30)
	void testFindByIdAsOtherUser() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID+1)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/"+(USER_ID+1)+"/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(30)
	void testFindByIdWithIdUserInvalid() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("/api/user/v1/0/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(30)
	void testFindByIdWithIdAddressInvalid() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
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
        assertEquals("/api/user/v1/"+USER_ID+"/address/0", output.getInstance());
	}

	@Test
	@Order(30)
	void testFindByIdWithIdAddressWrong() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID+1)
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
        assertEquals("/api/user/v1/"+(USER_ID)+"/address/"+(USER_ADDRESS_ID+1), output.getInstance());
	}

	@Test
	@Order(40)
	void testDeleteAsSameUser() {

		given()
			.spec(specification)
				.pathParam("user-id", USER_ID)
				.pathParam("address-id", USER_ADDRESS_ID)
			.when()
				.delete("/{user-id}/address/{address-id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
					.extract()
						.body()
							.asString();
	}

	@Test
	@Order(40)
	void testDeleteAsOtherUser() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID+1)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("The user is not authorized to access this resource", output.getDetail());
        assertEquals("/api/user/v1/"+(USER_ID+1)+"/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(40)
	void testDeleteWithIdUserInvalid() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", 0)
					.pathParam("address-id", USER_ADDRESS_ID)
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
        assertEquals("/api/user/v1/0/address/"+USER_ADDRESS_ID, output.getInstance());
	}

	@Test
	@Order(40)
	void testDeleteWithIdAddressInvalid() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
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
        assertEquals("/api/user/v1/"+USER_ID+"/address/0", output.getInstance());
	}

	@Test
	@Order(40)
	void testDeleteWithIdAddressWrong() {

		ExceptionResponse output =
			given()
				.spec(specification)
					.pathParam("user-id", USER_ID)
					.pathParam("address-id", USER_ADDRESS_ID+1)
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
        assertEquals("/api/user/v1/"+(USER_ID)+"/address/"+(USER_ADDRESS_ID+1), output.getInstance());
	}

	@Test
	@Order(1000)
	void removeUserTest() {
		given()
			.spec(specification)
			    .contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", USER_ID)
			.when()
				.delete("{id}")
			.then()
				.statusCode(HttpStatus.NO_CONTENT.value())
			.extract()
				.body()
					.asString();
	}
}
