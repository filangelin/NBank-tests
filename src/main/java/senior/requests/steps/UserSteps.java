package senior.requests.steps;

import io.restassured.specification.RequestSpecification;
import senior.models.AccountResponseModel;
import senior.models.deposit.MakeDepositRequestModel;
import senior.models.profile.GetProfileResponseModel;
import senior.requests.skelethon.Endpoint;
import senior.requests.skelethon.requesters.CrudRequester;
import senior.requests.skelethon.requesters.ValidatedCrudRequester;
import senior.specs.ResponseSpecs;

import java.util.List;
import java.util.Objects;

public class UserSteps {

    public static String getProfileName(RequestSpecification requestSpecs) {
        return new ValidatedCrudRequester<GetProfileResponseModel>(
                requestSpecs,
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get()
                .getName();
    }

    public static float getCurrentAccountBalance(RequestSpecification requestSpecs, Long accountId) {
        List<AccountResponseModel> accounts = new CrudRequester(
                requestSpecs,
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
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
                ResponseSpecs.entityWasCreated())
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
                    middle.iteration1.specs.ResponseSpecs.requestReturnsOK()
            ).post(request);

            remaining -= part;
        }
    }
}

