package senior.requests.steps;

import senior.generators.RandomModelGenerator;
import senior.models.CreateUserRequest;
import senior.models.CreateUserResponse;
import senior.requests.skelethon.Endpoint;
import senior.requests.skelethon.requesters.ValidatedCrudRequester;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;

public class AdminSteps {
    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }
}
