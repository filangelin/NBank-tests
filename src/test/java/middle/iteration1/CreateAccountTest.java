package middle.iteration1;

import middle.iteration1.generators.RandomData;
import middle.iteration1.models.CreateUserRequest;
import middle.iteration1.models.UserRole;
import org.junit.jupiter.api.Test;
import middle.iteration1.requests.AdminCreateUserRequester;
import middle.iteration1.requests.CreateAccountRequester;
import middle.iteration1.specs.RequestSpecs;
import middle.iteration1.specs.ResponseSpecs;

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
