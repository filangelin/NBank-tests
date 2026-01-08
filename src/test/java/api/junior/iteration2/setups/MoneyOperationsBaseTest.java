package api.junior.iteration2.setups;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.given;

public class MoneyOperationsBaseTest extends BaseTest {
    protected static final String ACCOUNT_URL = "/api/v1/accounts";
    protected static final String CUSTOMER_ACCOUNT_URL = "/api/v1/customer/accounts";
    protected static final String DEPOSIT = "/api/v1/accounts/deposit";
    protected static final String TRANSFER = "/api/v1/accounts/transfer";


    protected static List<Integer> user1Accounts = new ArrayList<>();
    protected static Integer user2Account;

    @BeforeAll
    protected static void setupAccounts() {
        //создание счетов для первого пользователя
        for (int i = 0; i < 2; i++) {
            int accountId = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", userToken)
                    .when()
                    .post(ACCOUNT_URL)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CREATED)
                    .extract()
                    .body()
                    .path("id");
            user1Accounts.add(accountId);
        }

        //логин второго пользователя
        String user2Token = getUser2Token();

        //cоздание счета второго пользователя
        user2Account = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user2Token)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .body()
                .path("id");

        //пополнение счета аккаунта, с которого будут осуществлятьсся переводы
        for (int i = 0; i < 3; i++) {
            given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", userToken)
                    .body(String.format(Locale.US,
                            """
                                    {
                                        "id": %d,
                                        "balance": 5000
                                    }
                                    """, user1Accounts.getFirst()))
                    .when()
                    .post(DEPOSIT)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
        }
    }

    protected static String getUser2Token() {
        return given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "username": "User2",
                        "password": "Password2!"
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


    protected static BigDecimal getCurrentBalance(int accountId, String token) {
        String amountStr = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .get(CUSTOMER_ACCOUNT_URL)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path(String.format("find { it.id == %d }.balance", accountId)).toString();

        return new BigDecimal(amountStr);
    }
}