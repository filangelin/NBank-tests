package senior.iteration2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import senior.common.Errors;
import senior.iteration1.BaseTest;
import senior.models.CreateUserRequest;
import senior.common.ResponseMessages;
import senior.models.comparison.ModelAssertions;
import senior.models.transfer.TransferMoneyRequestModel;
import senior.models.transfer.TransferMoneyResponseModel;
import senior.requests.skelethon.Endpoint;
import senior.requests.skelethon.requesters.CrudRequester;
import senior.requests.skelethon.requesters.ValidatedCrudRequester;
import senior.requests.steps.AdminSteps;
import senior.requests.steps.UserSteps;
import senior.specs.ResponseSpecs;

import java.util.stream.Stream;

import static middle.iteration1.generators.RandomData.getTransferAmount;
import static senior.common.Errors.*;

public class MoneyTransferTest extends BaseTest {
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
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = senior.specs.RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = senior.specs.RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        //пополнение баланса
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

        float senderExpectedBalance = senderInitialBalance - amount;
        float receiverExpectedBalance = receiverInitialBalance + amount;

        TransferMoneyRequestModel request = TransferMoneyRequestModel.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amount)
                .build();

        //отправка запроса и сообщении об успехе
        TransferMoneyResponseModel response = new ValidatedCrudRequester<TransferMoneyResponseModel>(
                reqSpec1,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOKWithMessage(ResponseMessages.SUCCESS_TRANSFER))
                .post(request);

        //проверка ответа
        ModelAssertions.assertThatModels(request, response).match();

        //проверка изменения баланса
        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverExpectedBalance);
    }


    @Test
    public void userCanTransferMoneyBetweenOwnAccounts() {
        //cоздание пользователя и его аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var reqSpec1 = senior.specs.RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec1).getId();
        //пополнение баланса
        float amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId2);

        float senderExpectedBalance = senderInitialBalance - amount;
        float receiverExpectedBalance = receiverInitialBalance + amount;

        var request = TransferMoneyRequestModel.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amount)
                .build();

        //отправка запроса и сообщении об успехе
        TransferMoneyResponseModel response = new ValidatedCrudRequester<TransferMoneyResponseModel>(
                reqSpec1,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOKWithMessage(ResponseMessages.SUCCESS_TRANSFER))
                .post(request);

        //проверка ответа
        ModelAssertions.assertThatModels(request, response).match();

        //проверка изменения баланса
        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverExpectedBalance);
    }


    private static Stream<Arguments> invalidDataForTransfer() {
        return Stream.of(
                //граничное значение 10000.01
                Arguments.of(11000f, 10000.01F, EXCEEDED_TRANSFER),
                //граничное значение 0
                Arguments.of(getTransferAmount(), 0, LEAST_TRANSFER),
                //user cannot transfer negative amount
                Arguments.of(getTransferAmount(), -100, LEAST_TRANSFER),
                //user cannot transfer more than sender balance
                Arguments.of(100, 200, INSUFFICIENT_FUND)
                );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForTransfer")
    public void userCannotTransferMoneyWithInvalidData(float depositAmount, float amount, Errors message) {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = senior.specs.RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = senior.specs.RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        UserSteps.depositMoney(reqSpec1, accountId1, depositAmount);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

        var request = TransferMoneyRequestModel.builder()
                .senderAccountId(accountId1)
                .receiverAccountId(accountId2)
                .amount(amount)
                .build();

        //отправка запроса и сообщении об ошибке
        new CrudRequester(
                reqSpec1,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(message))
                .post(request);

        //проверка, что балансы отправителя и получителя не изменились
        float senderUpdatedBalance =  UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance =  UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferFromNotOwnAccount() {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = senior.specs.RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = senior.specs.RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        float amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec2, accountId2, amount);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

        var request = TransferMoneyRequestModel.builder()
                .senderAccountId(accountId2)
                .receiverAccountId(accountId1)
                .amount(amount)
                .build();

        //отправка запроса и сообщении об ошибке
        new CrudRequester(
                reqSpec1,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsForbidden())
                .post(request);

        //проверка, что балансы отправителя и получателя не изменились
        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferToNonExistingAccount() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var reqSpec1 = senior.specs.RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);

        var request = TransferMoneyRequestModel.builder()
                .senderAccountId(9999)
                .receiverAccountId(accountId1)
                .amount(getTransferAmount())
                .build();

        //отправка запроса и сообщении об ошибке
        new CrudRequester(
                reqSpec1,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsForbidden())
                .post(request);

        //проверка, что баланс не изменилcя
        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
    }
}