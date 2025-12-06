package senior.iteration2;

import middle.iteration1.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import senior.iteration1.BaseTest;
import senior.models.AccountResponseModel;
import senior.models.CreateUserRequest;
import senior.models.deposit.MakeDepositRequestModel;
import senior.models.comparison.ModelAssertions;
import senior.requests.skelethon.Endpoint;
import senior.requests.skelethon.requesters.CrudRequester;
import senior.requests.skelethon.requesters.ValidatedCrudRequester;
import senior.requests.steps.AdminSteps;
import senior.requests.steps.UserSteps;
import senior.specs.RequestSpecs;

import java.util.stream.Stream;

import static middle.iteration1.generators.RandomData.getDepositAmount;


public class DepositTest extends BaseTest {

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
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        Long accountId = UserSteps.createAccount(reqSpec).getId();
        float currentBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        float expectedBalance = currentBalance + depositAmount;

        var request = new MakeDepositRequestModel(accountId, depositAmount);

        //отправка запроса
        AccountResponseModel response = new ValidatedCrudRequester<AccountResponseModel>(
                reqSpec,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(request);

        ModelAssertions.assertThatModels(request, response).match();

        //проверка, что баланс поменялся на ожидаемый через GET метод
        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
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
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        Long accountId = UserSteps.createAccount(reqSpec).getId();
        float initialBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);

        var request = new MakeDepositRequestModel(accountId, deposit);

        //отправка запроса и проверка сообщения об ошибке
        new CrudRequester(
                reqSpec,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(message))
                .post(request);

        //проверка, что баланс не поменялся
        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
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
        float initialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId);

        var request = new MakeDepositRequestModel(accountId, getDepositAmount());

        //отправка запроса и проверка сообщения об ошибке
        new CrudRequester(
                reqSpec1,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(request);


        //проверка, что баланс другого пользователя не поменялся
        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId);
        softly.assertThat(updatedBalance).isEqualTo(initialBalance);
    }
}