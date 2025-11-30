package senior.iteration1;

import senior.models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import senior.requests.skelethon.Endpoint;
import senior.requests.skelethon.requesters.CrudRequester;
import senior.requests.steps.AdminSteps;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;

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
