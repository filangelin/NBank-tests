package middle.iteration2.setups;

import middle.iteration1.generators.RandomData;
import middle.iteration1.models.CreateUserRequest;
import middle.iteration1.models.UserRole;
import middle.iteration1.requests.AdminCreateUserRequester;
import middle.iteration1.specs.RequestSpecs;
import middle.iteration1.specs.ResponseSpecs;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions softly;
    protected CreateUserRequest user1;
    protected CreateUserRequest user2;

    @BeforeEach
    public void setup() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
    }

    @BeforeEach
    public void createUsers() {
        user1 = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .sendRequest(user1);


        user2 = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .sendRequest(user2);
    }
}