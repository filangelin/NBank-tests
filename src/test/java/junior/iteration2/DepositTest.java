package junior.iteration2;

import io.restassured.http.ContentType;
import junior.iteration2.setups.MoneyOperationsBaseTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class DepositTest extends MoneyOperationsBaseTest {

    @ParameterizedTest
    @CsvSource({
            //граничные значения
            "0.01", "5000"
    })
    public void userCanDepositToOwnAccount(double deposit) {
        int accountId = user1Accounts.getFirst();

        BigDecimal currentBalance = getCurrentBalance(accountId, userToken);
        BigDecimal depositAmount = BigDecimal.valueOf(deposit);
        BigDecimal expectedBalance = currentBalance.add(depositAmount);


        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body(String.format(Locale.US,
                        """
                        {
                            "id": %d,
                            "balance": %.2f
                        }
                        """, accountId, depositAmount))
                .when()
                .post(DEPOSIT)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        BigDecimal updatedBalance = getCurrentBalance(accountId, userToken);
        Assertions.assertEquals(0, expectedBalance.compareTo(updatedBalance));

    }


    private static Stream<Arguments> invalidDataForDeposit() {
        return Stream.of(
                //граничные значения суммы депозита
                Arguments.of(user1Accounts.getFirst(), 0, 400, "Deposit amount must be at least 0.01"),
                Arguments.of(user1Accounts.getFirst(), 5000.01, 400, "Deposit amount cannot exceed 5000"),
                //отрицательное значение депозита
                Arguments.of(user1Accounts.getFirst(), -100, 400, "Deposit amount must be at least 0.01")

                );
    }
    @ParameterizedTest
    @MethodSource("invalidDataForDeposit")
    public void userCannotDepositWithInvalidData(int accountId, double deposit, int statusCode, String message) {
        BigDecimal currentBalance = getCurrentBalance(accountId, userToken);
        BigDecimal depositAmount = BigDecimal.valueOf(deposit);
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body(String.format(Locale.US,
                        """
                        {
                            "id": %d,
                            "balance": %.2f
                        }
                        """, accountId, depositAmount))
                .when()
                .post(DEPOSIT)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .body(Matchers.containsString(message));

        BigDecimal updatedBalance = getCurrentBalance(accountId, userToken);
        Assertions.assertEquals(0, currentBalance.compareTo(updatedBalance));

    }


    private static Stream<Arguments> invalidDataForDepositWithUncheckableBalance() {
        return Stream.of(
                //счет другого пользователя
                Arguments.of(user2Account, 100, 403, "Unauthorized access to account"),
                //несуществующий счет
                Arguments.of(99999, 100, 403, "Unauthorized access to account"));
    }

    @ParameterizedTest
    @MethodSource("invalidDataForDepositWithUncheckableBalance")
    public void userCannotDepositWithInvalidDataAndUncheckableBalance(int accountId, double deposit, int statusCode, String message) {
        BigDecimal depositAmount = BigDecimal.valueOf(deposit);
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body(String.format(Locale.US,
                        """
                                {
                                    "id": %d,
                                    "balance": %.2f
                                }
                                """, accountId, depositAmount))
                .when()
                .post(DEPOSIT)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .body(Matchers.containsString(message));

    }
}
