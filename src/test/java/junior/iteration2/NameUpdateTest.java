package junior.iteration2;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import io.restassured.http.ContentType;
import junior.iteration2.setups.BaseTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class NameUpdateTest extends BaseTest {
    private final String PROFILE = "/api/v1/customer/profile";


    @Test
    public void userCanChangeNameWithValidName() {
        String currentName = getCurrentName();
        Name fakerName = new Faker().name();
        String updatedName = fakerName.firstName() + " " + fakerName.lastName();

        //запрос на изменение
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body("""
                        {
                          "name": "%s"
                        }
                        """.formatted(updatedName))
                .when()
                .put(PROFILE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.equalTo("Profile updated successfully"))
                .body("customer.name", Matchers.equalTo(updatedName));

        //проверка изменения имени в профиле
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .when()
                .get(PROFILE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("name", Matchers.equalTo(updatedName))
                .body("name", Matchers.not(Matchers.equalTo(currentName)));

    }

    public static Stream<Arguments> invalidDataForNameUpdating() {
        return Stream.of(
                //Less than two words
                Arguments.of("name"),
                //More than two words
                Arguments.of("One Two Three"),
                //name with more than one space
                Arguments.of("new  name"),
                //name containing digits
                Arguments.of("new name1"),
                //name containing special characters
                Arguments.of("new name!")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForNameUpdating")
    public void userCannotChangeNameWithInvalidName(String updatedName) {
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body("""
                        {
                          "name": "%s"
                        }
                        """.formatted(updatedName))
                .when()
                .put(PROFILE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Name must contain two words with letters only"));

        //проверка, что имя в профиле не поменялось
        String currentName = getCurrentName();
        Assertions.assertNotEquals(updatedName, currentName);

    }

    private String getCurrentName() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .when()
                .get(PROFILE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("name");
    }

}
