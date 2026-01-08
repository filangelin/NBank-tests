package api.middle.iteration2;

import api.middle.iteration1.specs.RequestSpecs;
import api.middle.iteration1.specs.ResponseSpecs;
import api.middle.iteration2.models.TransferMoneyRequestModel;
import api.middle.iteration2.models.TransferMoneyResponseModel;
import api.middle.iteration2.requesters.TransferMoneyRequester;
import api.middle.iteration2.setups.MoneyOperationsBaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static api.middle.iteration1.generators.RandomData.getTransferAmount;

public class MoneyTransferTest extends MoneyOperationsBaseTest {
    private static Stream<Arguments> correctDataForTransfer() {
        return Stream.of(
                //user can transfer money to another user account(maximum 10000)
                Arguments.of(10000),
                Arguments.of(9999.99F),
                //min 0.01
                Arguments.of(0.01F),
                Arguments.of(0.02F)
        );
    }

    @ParameterizedTest
    @MethodSource("correctDataForTransfer")
    public void userCanTransferMoney(float amount) {
        float senderInitialBalance = getCurrentBalance(user1FirstAccount, user1);
        float receiverInitialBalance = getCurrentBalance(user2FirstAccount, user2);

        float senderExpectedBalance = senderInitialBalance - amount;
        float receiverExpectedBalance = receiverInitialBalance + amount;

        //отправка запроса и сообщении об успехе
        TransferMoneyResponseModel response = new TransferMoneyRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsOK("message", "Transfer successful"))
                .sendRequest(TransferMoneyRequestModel.builder()
                        .senderAccountId(user1FirstAccount)
                        .receiverAccountId(user2FirstAccount)
                        .amount(amount)
                        .build())
                .extract().as(TransferMoneyResponseModel.class);

        //проверка ответа
        softly.assertThat(response.getAmount()).isEqualTo(amount);

        //проверка изменения баланса
        float senderUpdatedBalance = getCurrentBalance(user1FirstAccount, user1);
        float receiverUpdatedBalance = getCurrentBalance(user2FirstAccount, user2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverExpectedBalance);
    }


    @Test
    public void userCanTransferMoneyBetweenOwnAccounts() {
        float senderInitialBalance = getCurrentBalance(user1FirstAccount, user1);
        float receiverInitialBalance = getCurrentBalance(user1SecondAccount, user1);
        float amount = getTransferAmount();

        float senderExpectedBalance = senderInitialBalance - amount;
        float receiverExpectedBalance = receiverInitialBalance + amount;

        //отправка запроса и сообщении об успехе
        TransferMoneyResponseModel response = new TransferMoneyRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsOK("message", "Transfer successful"))
                .sendRequest(TransferMoneyRequestModel.builder()
                        .senderAccountId(user1FirstAccount)
                        .receiverAccountId(user1SecondAccount)
                        .amount(amount)
                        .build())
                .extract().as(TransferMoneyResponseModel.class);

        //проверка ответа
        softly.assertThat(response.getAmount()).isEqualTo(amount);

        //проверка изменения баланса
        float senderUpdatedBalance = getCurrentBalance(user1FirstAccount, user1);
        float receiverUpdatedBalance = getCurrentBalance(user1SecondAccount, user1);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverExpectedBalance);
    }


    private static Stream<Arguments> invalidDataForTransfer() {
        return Stream.of(
                //граничное значение 10000.01
                Arguments.of(10000.01F, "Transfer amount cannot exceed 10000"),
                //граничное значение 0
                Arguments.of(0, "Transfer amount must be at least 0.01"),
                //user cannot transfer negative amount
                Arguments.of(-100, "Transfer amount must be at least 0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForTransfer")
    public void userCannotTransferMoneyWithInvalidData(float amount, String message) {
        float senderInitialBalance = getCurrentBalance(user1FirstAccount, user1);
        float receiverInitialBalance = getCurrentBalance(user2FirstAccount, user2);

        //отправка запроса и сообщении об ошибке
        new TransferMoneyRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(message))
                .sendRequest(TransferMoneyRequestModel.builder()
                        .senderAccountId(user1FirstAccount)
                        .receiverAccountId(user2FirstAccount)
                        .amount(amount)
                        .build());

        //проверка, что балансы отправителя и получителя не изменились
        float senderUpdatedBalance = getCurrentBalance(user1FirstAccount, user1);
        float receiverUpdatedBalance = getCurrentBalance(user2FirstAccount, user2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferMoreThanSenderBalance() {
        float senderInitialBalance = getCurrentBalance(user2FirstAccount, user2);
        float receiverInitialBalance = getCurrentBalance(user1FirstAccount, user1);

        //отправка запроса и сообщении об ошибке
        new TransferMoneyRequester(RequestSpecs.authAsUser(user2.getUsername(), user2.getPassword()),
                ResponseSpecs.requestReturnsBadRequest("insufficient funds or invalid accounts"))
                .sendRequest(TransferMoneyRequestModel.builder()
                        .senderAccountId(user2FirstAccount)
                        .receiverAccountId(user1FirstAccount)
                        .amount(getTransferAmount())
                        .build());

        //проверка, что балансы отправителя и получителя не изменились
        float senderUpdatedBalance = getCurrentBalance(user2FirstAccount, user2);
        float receiverUpdatedBalance = getCurrentBalance(user1FirstAccount, user1);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferFromNotOwnAccount() {
        float senderInitialBalance = getCurrentBalance(user2FirstAccount, user2);
        float receiverInitialBalance = getCurrentBalance(user1SecondAccount, user1);

        //отправка запроса и сообщении об ошибке
        new TransferMoneyRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsForbidden())
                .sendRequest(TransferMoneyRequestModel.builder()
                        .senderAccountId(user2FirstAccount)
                        .receiverAccountId(user1SecondAccount)
                        .amount(getTransferAmount())
                        .build());

        //проверка, что балансы отправителя и получателя не изменились
        float senderUpdatedBalance = getCurrentBalance(user2FirstAccount, user2);
        float receiverUpdatedBalance = getCurrentBalance(user1SecondAccount, user1);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferToNonExistingAccount() {
        float senderInitialBalance = getCurrentBalance(user1FirstAccount, user1);

        //отправка запроса и сообщении об ошибке
        new TransferMoneyRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsBadRequest("insufficient funds or invalid accounts"))
                .sendRequest(TransferMoneyRequestModel.builder()
                        .senderAccountId(user1FirstAccount)
                        .receiverAccountId(9999)
                        .amount(getTransferAmount())
                        .build());

        //проверка, что баланс отправителя не изменилcя
        float senderUpdatedBalance = getCurrentBalance(user1FirstAccount, user1);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
    }
}