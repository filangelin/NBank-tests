package junior.iteration2;

import io.restassured.http.ContentType;
import junior.iteration2.setups.MoneyOperationsBaseTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class MoneyTransferTest extends MoneyOperationsBaseTest {
    private static Stream<Arguments> correctDataForTransfer() {
        return Stream.of(
                //user can transfer money between own accounts
                Arguments.of(user1Accounts.getFirst(), user1Accounts.getLast(), 100, userToken),
                //user can transfer money to another user account(maximum 10000)
                Arguments.of(user1Accounts.getFirst(), user2Account, 10000, getUser2Token()),
                Arguments.of(user1Accounts.getFirst(), user2Account, 0.01, getUser2Token())
        );
    }

    @ParameterizedTest
    @MethodSource("correctDataForTransfer")
    public void userCanTransferMoney(int senderAccountId, int receiverAccountId, double amount, String receiverToken) {
        BigDecimal senderCurrentBalance = getCurrentBalance(senderAccountId, userToken);
        BigDecimal receiverCurrentBalance = getCurrentBalance(receiverAccountId, receiverToken);
        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        BigDecimal senderExpectedBalance = senderCurrentBalance.subtract(transferAmount);
        BigDecimal receiverExpectedBalance = receiverCurrentBalance.add(transferAmount);

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body(String.format(Locale.US,
                        """
                                {
                                   "senderAccountId": %d,
                                   "receiverAccountId": %d,
                                   "amount": %.2f
                                 }
                                """, senderAccountId, receiverAccountId, amount))
                .when()
                .post(TRANSFER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.equalTo("Transfer successful"));

        BigDecimal senderUpdatedBalance = getCurrentBalance(senderAccountId, userToken);
        BigDecimal receiverUpdatedBalance = getCurrentBalance(receiverAccountId, receiverToken);
        Assertions.assertEquals(0, senderExpectedBalance.compareTo(senderUpdatedBalance));
        Assertions.assertEquals(0, receiverExpectedBalance.compareTo(receiverUpdatedBalance));
    }


    private static Stream<Arguments> invalidDataForTransfer() {
        return Stream.of(
                //user cannot transfer to the same account
                Arguments.of(user1Accounts.getFirst(), user1Accounts.getFirst(), 100, 400, "", userToken),
                //user cannot transfer more than 10000
                Arguments.of(user1Accounts.getFirst(), user2Account, 10000.01, 400, "Transfer amount cannot exceed 10000", getUser2Token()),
                //user cannot transfer 0
                Arguments.of(user1Accounts.getFirst(), user2Account, 0, 400, "Transfer amount must be at least 0.01", getUser2Token()),
                //user cannot transfer negative amount
                Arguments.of(user1Accounts.getFirst(), user2Account, -100, 400, "Transfer amount must be at least 0.01", getUser2Token()),
                //user cannot transfer more than sender balance
                Arguments.of(user1Accounts.getLast(), user2Account, 10000, 400, "insufficient funds or invalid accounts", getUser2Token())
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForTransfer")
    public void userCannotTransferMoneyWithInvalidData(int senderAccountId, int receiverAccountId, double amount, int statusCode, String message,
                                                       String receiverToken) {
        BigDecimal senderCurrentBalance = getCurrentBalance(senderAccountId, userToken);
        BigDecimal receiverCurrentBalance = getCurrentBalance(receiverAccountId, receiverToken);

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body(String.format(Locale.US,
                        """
                                {
                                   "senderAccountId": %d,
                                   "receiverAccountId": %d,
                                   "amount": %.2f
                                 }
                                """, senderAccountId, receiverAccountId, amount))
                .when()
                .post(TRANSFER)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .body(Matchers.containsString(message));

        BigDecimal senderUpdatedBalance = getCurrentBalance(senderAccountId, userToken);
        BigDecimal receiverUpdatedBalance = getCurrentBalance(receiverAccountId, receiverToken);
        Assertions.assertEquals(0, senderCurrentBalance.compareTo(senderUpdatedBalance));
        Assertions.assertEquals(0, receiverCurrentBalance.compareTo(receiverUpdatedBalance));
    }


    private static Stream<Arguments> invalidDataForTransferWithUncheckableBalance() {
        return Stream.of(
                //user cannot transfer from nonexisting account
                Arguments.of(99999, user2Account, 100, 403, "Unauthorized access to account", getUser2Token()),
                //user cannot transfer to nonexisting account
                Arguments.of(user1Accounts.getFirst(), 99999, 100, 400, "insufficient funds or invalid accounts", getUser2Token()),
                //user cannot transfer from not own account
                Arguments.of(user2Account, user1Accounts.getFirst(), 100, 403, "Unauthorized access to account", getUser2Token()));
    }

    @ParameterizedTest
    @MethodSource("invalidDataForTransferWithUncheckableBalance")
    public void userCannotTransferMoneyWithInvalidDataAndUncheckable(int senderAccountId, int receiverAccountId, double amount, int statusCode, String message,
                                                       String receiverToken) {

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userToken)
                .body(String.format(Locale.US,
                        """
                                {
                                   "senderAccountId": %d,
                                   "receiverAccountId": %d,
                                   "amount": %.2f
                                 }
                                """, senderAccountId, receiverAccountId, amount))
                .when()
                .post(TRANSFER)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .body(Matchers.containsString(message));
    }
}
