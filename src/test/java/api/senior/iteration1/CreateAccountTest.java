package api.senior.iteration1;

import api.senior.models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import api.senior.requests.skelethon.Endpoint;
import api.senior.requests.skelethon.requesters.CrudRequester;
import api.senior.requests.steps.AdminSteps;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        // запросить все аккаунты пользователя и проверить, что наш аккаунт там
    }
}
