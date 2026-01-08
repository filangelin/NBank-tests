package api.senior.requests.steps;

import api.middle.iteration1.specs.ResponseSpecs;
import api.senior.models.CreateAccountResponse;
import api.senior.specs.RequestSpecs;
import io.restassured.specification.RequestSpecification;
import api.senior.models.AccountResponseModel;
import api.senior.models.deposit.MakeDepositRequestModel;
import api.senior.models.profile.GetProfileResponseModel;
import api.senior.requests.skelethon.Endpoint;
import api.senior.requests.skelethon.requesters.CrudRequester;
import api.senior.requests.skelethon.requesters.ValidatedCrudRequester;

import java.util.List;
import java.util.Objects;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public  List<CreateAccountResponse> getAllAccounts() {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()).getAll(CreateAccountResponse[].class);
    }

    public static String getProfileName(RequestSpecification requestSpecs) {
        return new ValidatedCrudRequester<GetProfileResponseModel>(
                requestSpecs,
                Endpoint.GET_PROFILE,
                api.senior.specs.ResponseSpecs.requestReturnsOK())
                .get()
                .getName();
    }

    public static float getCurrentAccountBalance(RequestSpecification requestSpecs, Long accountId) {
        List<AccountResponseModel> accounts = new CrudRequester(
                requestSpecs,
                Endpoint.CUSTOMER_ACCOUNTS,
                api.senior.specs.ResponseSpecs.requestReturnsOK())
                .get()
                .extract().jsonPath().getList("", AccountResponseModel.class);

        return accounts.stream()
                .filter(it -> Objects.equals(accountId, it.getId()))
                .map(AccountResponseModel::getBalance)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Account with id = %d not found", accountId)));
    }

    public static AccountResponseModel createAccount(RequestSpecification reqSpec) {
        return new ValidatedCrudRequester<AccountResponseModel>(
                reqSpec,
                Endpoint.ACCOUNTS,
                api.senior.specs.ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public static void depositMoney(RequestSpecification reqSpec, Long accountId, float amount) {
        final float MAX_DEPOSIT = 5000f;
        float remaining = amount;
        while (remaining > 0) {
            float part = Math.min(remaining, MAX_DEPOSIT);

            MakeDepositRequestModel request = new MakeDepositRequestModel(accountId, part);
            new ValidatedCrudRequester<AccountResponseModel>(
                    reqSpec,
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOK()
            ).post(request);

            remaining -= part;
        }
    }
}

