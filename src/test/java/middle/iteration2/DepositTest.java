package middle.iteration2;

import middle.iteration1.specs.RequestSpecs;
import middle.iteration1.specs.ResponseSpecs;
import middle.iteration2.models.MakeDepositRequestModel;
import middle.iteration2.requesters.MakeDepositRequester;
import middle.iteration2.setups.MoneyOperationsBaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static middle.iteration1.generators.RandomData.getDepositAmount;


public class DepositTest extends MoneyOperationsBaseTest {

    private static Stream<Arguments> validDataForDeposit() {
        return Stream.of(
                //граничные значения
                Arguments.of(0.01F),
                Arguments.of(0.02F),
                Arguments.of(4999.99F),
                Arguments.of(5000)
        );
    }

    @ParameterizedTest
    @MethodSource("validDataForDeposit")
    public void userCanDepositToOwnAccount(float depositAmount) {
        float currentBalance = getCurrentBalance(user1FirstAccount, user1);
        float expectedBalance = currentBalance+depositAmount;

        //отправка запроса
        new MakeDepositRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .sendRequest(new MakeDepositRequestModel(user1FirstAccount, depositAmount));


        //проверка, что баланс поменялся на ожидаемый
        float updatedBalance = getCurrentBalance(user1FirstAccount, user1);
        softly.assertThat(updatedBalance).isEqualTo(expectedBalance);

    }


    private static Stream<Arguments> invalidDataForDeposit() {
        return Stream.of(
                //граничные значения суммы депозита
                Arguments.of( 0,  "Deposit amount must be at least 0.01"),
                Arguments.of( 5000.01F,  "Deposit amount cannot exceed 5000"),
                //отрицательное значение депозита
                Arguments.of( -100, "Deposit amount must be at least 0.01")
                );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForDeposit")
    public void userCannotDepositWithInvalidData(float deposit, String message) {
        float initialBalance = getCurrentBalance(user1FirstAccount, user1);

        //отправка запроса и проверка сообщения об ошибке
        new MakeDepositRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(message))
                .sendRequest(new MakeDepositRequestModel(user1FirstAccount, deposit));

        //проверка, что баланс не поменялся
        float updatedBalance = getCurrentBalance(user1FirstAccount, user1);
        softly.assertThat(updatedBalance).isEqualTo(initialBalance);
    }


    @Test
    public void userCannotDepositToSomeoneElseAccount() {
        //счет другого пользователя
        float initialBalance = getCurrentBalance(user2FirstAccount, user2);

        //отправка запроса и проверка сообщения об ошибке
        new MakeDepositRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsForbidden())
                .sendRequest(new MakeDepositRequestModel(user2FirstAccount, getDepositAmount()));

        //проверка, что баланс другого пользователя не поменялся
        float updatedBalance = getCurrentBalance(user2FirstAccount, user2);
        softly.assertThat(updatedBalance).isEqualTo(initialBalance);
    }
}