package middle.iteration1;

import middle.generators.RandomData;
import middle.models.CreateUserRequest;
import middle.models.UserRole;
import org.junit.jupiter.api.Test;
import middle.requests.AdminCreateUserRequester;
import middle.requests.CreateAccountRequester;
import middle.specs.RequestSpecs;
import middle.specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .post(null);

        // запросить все аккаунты пользователя и проверить, что наш аккаунт там

    }
}
