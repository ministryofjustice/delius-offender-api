package uk.gov.justice.digital.delius;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.justice.digital.delius.jpa.entity.User;
import uk.gov.justice.digital.delius.jpa.repository.OffenderRepository;
import uk.gov.justice.digital.delius.jpa.repository.UserRepository;
import uk.gov.justice.digital.delius.jwt.Jwt;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class LogonAPITest {

    @LocalServerPort
    int port;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Jwt jwt;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (aClass, s) -> objectMapper
        ));

        when(userRepository.findByDistinguishedName("jihn")).thenReturn(Optional.of(aUser()));
        when(userRepository.findByDistinguishedName("jimmysnozzle")).thenReturn(Optional.empty());

    }

    private User aUser() {
        return User.builder()
                .distinguishedName("jihn")
                .forename("Jihn")
                .surname("Doe")
                .userId(1L)
                .build();
    }

    @Test
    public void logonWithMissingBodyGivesBadRequest() {
        given()
                .contentType("text/plain")
                .when()
                .post("/logon")
                .then()
                .statusCode(400);
    }

    @Test
    public void logonWithUnknownButOtherwiseValidDistinguishedNameGivesNotFound() {
        given()
                .body("uid=jimmysnozzle,ou=people,dc=memorynotfound,dc=com")
                .when()
                .post("/logon")
                .then()
                .statusCode(404);
    }

    @Test
    public void logonWithKnownDistinguishedNameGivesTokenContainingOracleUser() {
        String token = given()
                .body("uid=jihn,ou=people,dc=memorynotfound,dc=com")
                .when()
                .post("/logon")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(jwt.parseToken(token).get().get("deliusDistinguishedName")).isEqualTo("jihn");
    }
}