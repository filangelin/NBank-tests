package api.senior.iteration2;

import api.senior.specs.RequestSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.senior.common.Errors;
import api.senior.iteration1.BaseTest;
import api.senior.models.CreateUserRequest;
import api.senior.common.ResponseMessages;
import api.senior.models.comparison.ModelAssertions;
import api.senior.models.transfer.TransferMoneyRequestModel;
import api.senior.models.transfer.TransferMoneyResponseModel;
import api.senior.requests.skelethon.Endpoint;
import api.senior.requests.skelethon.requesters.CrudRequester;
import api.senior.requests.skelethon.requesters.ValidatedCrudRequester;
import api.senior.requests.steps.AdminSteps;
import api.senior.requests.steps.UserSteps;
import api.senior.specs.ResponseSpecs;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static api.middle.iteration1.generators.RandomData.getNonexistingAccountId;
import static api.middle.iteration1.generators.RandomData.getTransferAmount;
import static api.senior.common.Errors.*;

public class MoneyTransferTest extends BaseTest {
    private static Stream<Arguments> correctDataForTransfer() {
        return Stream.of(
                //user can transfer money to another user account(maximum 10000)
                Arguments.of(new BigDecimal("10000.00")),
                Arguments.of(new BigDecimal("9999.99")),
                // min 0.01
                Arguments.of(new BigDecimal("0.01")),
                Arguments.of(new BigDecimal("0.02"))
        );
    }

    @ParameterizedTest
    @MethodSource("correctDataForTransfer")
    public void userCanTransferMoney(BigDecimal amount) {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        //пополнение баланса
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        BigDecimal senderInitialBalance =
                UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);

        BigDecimal receiverInitialBalance =
                UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

        BigDecimal senderExpectedBalance =
                senderInitialBalance.subtract(amount);

        BigDecimal receiverExpectedBalance =
                receiverInitialBalance.add(amount);


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
        BigDecimal senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualByComparingTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualByComparingTo(receiverExpectedBalance);
    }


    @Test
    public void userCanTransferMoneyBetweenOwnAccounts() {
        //cоздание пользователя и его аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec1).getId();
        //пополнение баланса
        BigDecimal amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        BigDecimal senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId2);

        BigDecimal senderExpectedBalance = senderInitialBalance.subtract(amount);
        BigDecimal receiverExpectedBalance = receiverInitialBalance.add(amount);

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
        BigDecimal senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualByComparingTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualByComparingTo(receiverExpectedBalance);
    }


    private static Stream<Arguments> invalidDataForTransfer() {
        return Stream.of(
                //граничное значение 10000.01
                Arguments.of(new BigDecimal("11000.00"),
                        new BigDecimal("10000.01"),
                        EXCEEDED_TRANSFER),

                // граничное значение 0
                Arguments.of(getTransferAmount(),
                        BigDecimal.ZERO,
                        LEAST_TRANSFER),

                // user cannot transfer negative amount
                Arguments.of(getTransferAmount(),
                        new BigDecimal("-100.00"),
                        LEAST_TRANSFER),

                // user cannot transfer more than sender balance
                Arguments.of(new BigDecimal("100.00"),
                        new BigDecimal("200.00"),
                        INSUFFICIENT_FUND)
                );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForTransfer")
    public void userCannotTransferMoneyWithInvalidData(BigDecimal depositAmount, BigDecimal amount, Errors message) {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        UserSteps.depositMoney(reqSpec1, accountId1, depositAmount);

        BigDecimal senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

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
        BigDecimal senderUpdatedBalance =  UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverUpdatedBalance =  UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferFromNotOwnAccount() {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        BigDecimal amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec2, accountId2, amount);

        BigDecimal senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

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
        BigDecimal senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        BigDecimal receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferToNonExistingAccount() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();

        BigDecimal senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);

        var request = TransferMoneyRequestModel.builder()
                .senderAccountId(getNonexistingAccountId())
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
        BigDecimal senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
    }
}