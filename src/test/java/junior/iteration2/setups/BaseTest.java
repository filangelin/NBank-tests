package junior.iteration2.setups;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static io.restassured.RestAssured.given;


public class BaseTest {
    protected static final String BASE_URL = "http://localhost:4111";
    protected static final String LOGIN_URL = "/api/v1/auth/login";
    protected static String userToken;

    @BeforeAll
    protected static void setupRestAssured() {
        RestAssured.filters(List.of(new RequestLoggingFilter(),
                new ResponseLoggingFilter()));
    }

    @BeforeAll
    protected static void getUserToken() {
        userToken = given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "username": "User1",
                        "password": "Password1!"
                        }
                        """)
                .when()
                .post(LOGIN_URL)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .header("Authorization");
    }
}