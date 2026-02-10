package api.senior.iteration2;

import api.middle.iteration1.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.senior.common.Errors;
import api.senior.iteration1.BaseTest;
import api.senior.models.AccountResponseModel;
import api.senior.models.CreateUserRequest;
import api.senior.models.deposit.MakeDepositRequestModel;
import api.senior.models.comparison.ModelAssertions;
import api.senior.requests.skelethon.Endpoint;
import api.senior.requests.skelethon.requesters.CrudRequester;
import api.senior.requests.skelethon.requesters.ValidatedCrudRequester;
import api.senior.requests.steps.AdminSteps;
import api.senior.requests.steps.UserSteps;
import api.senior.specs.RequestSpecs;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static api.middle.iteration1.generators.RandomData.getDepositAmount;
import static api.senior.common.Errors.EXCEEDED_DEPOSIT;
import static api.senior.common.Errors.LEAST_DEPOSIT;


public class DepositTest extends BaseTest {

    private static Stream<Arguments> validDataForDeposit() {
        return Stream.of(
                // граничные значения
                Arguments.of(new BigDecimal("0.01")),
                Arguments.of(new BigDecimal("0.02")),
                Arguments.of(new BigDecimal("4999.99")),
                Arguments.of(new BigDecimal("5000.00"))
        );
    }

    @ParameterizedTest
    @MethodSource("validDataForDeposit")
    public void userCanDepositToOwnAccount(BigDecimal depositAmount) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        Long accountId = UserSteps.createAccount(reqSpec).getId();
        BigDecimal currentBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        BigDecimal expectedBalance = currentBalance.add(depositAmount);

        var request = new MakeDepositRequestModel(accountId, depositAmount);

        //отправка запроса
        AccountResponseModel response = new ValidatedCrudRequester<AccountResponseModel>(
                reqSpec,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        ModelAssertions.assertThatModels(request, response).match();

        //проверка, что баланс поменялся на ожидаемый через GET метод
        BigDecimal updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        softly.assertThat(updatedBalance).isEqualByComparingTo(expectedBalance);

    }


    private static Stream<Arguments> invalidDataForDeposit() {
        return Stream.of(
                //граничные значения суммы депозита
                Arguments.of(new BigDecimal("0.00"), LEAST_DEPOSIT),
                Arguments.of(new BigDecimal("5000.01"), EXCEEDED_DEPOSIT),
                // отрицательное значение депозита
                Arguments.of(new BigDecimal("-100.00"), LEAST_DEPOSIT)
        );

    }

    @ParameterizedTest
    @MethodSource("invalidDataForDeposit")
    public void userCannotDepositWithInvalidData(BigDecimal deposit, Errors message) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        Long accountId = UserSteps.createAccount(reqSpec).getId();
        BigDecimal initialBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);

        var request = new MakeDepositRequestModel(accountId, deposit);

        //отправка запроса и проверка сообщения об ошибке
        new CrudRequester(
                reqSpec,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(message))
                .post(request);

        //проверка, что баланс не поменялся
        BigDecimal updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        softly.assertThat(updatedBalance).isEqualTo(initialBalance);
    }


    @Test
    public void userCannotDepositToSomeoneElseAccount() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId = UserSteps.createAccount(reqSpec2).getId();
        //счет другого пользователя
        BigDecimal initialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId);

        var request = new MakeDepositRequestModel(accountId, getDepositAmount());

        //отправка запроса и проверка сообщения об ошибке
        new CrudRequester(
                reqSpec1,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(request);


        //проверка, что баланс другого пользователя не поменялся
        BigDecimal updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId);
        softly.assertThat(updatedBalance).isEqualTo(initialBalance);
    }
}