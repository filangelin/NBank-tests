package api.senior.iteration1;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.senior.models.AccountResponseModel;
import api.senior.models.CreateUserRequest;
import api.senior.requests.skelethon.requesters.ValidatedCrudRequester;
import api.senior.requests.steps.DataBaseSteps;
import org.junit.jupiter.api.Test;
import api.senior.requests.skelethon.Endpoint;
import api.senior.requests.steps.AdminSteps;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        AccountResponseModel createAccountResponse = new ValidatedCrudRequester<AccountResponseModel>
                (RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.ACCOUNTS,
                        ResponseSpecs.entityWasCreated())
                .post(null);

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(createAccountResponse.getAccountNumber());
        DaoAndModelAssertions.assertThat(createAccountResponse, accountDao).match();
    }
}
