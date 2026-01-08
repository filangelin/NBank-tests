package api.senior.iteration2;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import api.middle.iteration2.setups.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.senior.models.CreateUserRequest;
import api.senior.models.comparison.ModelAssertions;
import api.senior.models.profile.ChangeNameRequestModel;
import api.senior.models.profile.ChangeNameResponseModel;
import api.senior.requests.skelethon.Endpoint;
import api.senior.requests.skelethon.requesters.CrudRequester;
import api.senior.requests.skelethon.requesters.ValidatedCrudRequester;
import api.senior.requests.steps.AdminSteps;
import api.senior.requests.steps.UserSteps;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

import java.util.stream.Stream;

import static api.senior.common.Errors.INVALID_NAME;
import static api.senior.common.ResponseMessages.SUCCESS_PROFILE_UPDATE;


public class NameUpdateTest extends BaseTest {

    @Test
    public void userCanChangeNameWithValidName() {
        CreateUserRequest userRequest =  AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        String initialName = UserSteps.getProfileName(reqSpec);

        Name fakerName = new Faker().name();
        String nameForUpdate = fakerName.firstName() + " " + fakerName.lastName();
        ChangeNameRequestModel changeNameRequestModel = ChangeNameRequestModel.builder()
                .name(nameForUpdate)
                .build();

        //запрос на изменение
        ChangeNameResponseModel changeNameResponse = new ValidatedCrudRequester<ChangeNameResponseModel>(
                reqSpec,
                Endpoint.UPDATE_PROFILE,
                ResponseSpecs.requestReturnsOKWithMessage(SUCCESS_PROFILE_UPDATE))
                .put(changeNameRequestModel);


        //проверка ответа
        ModelAssertions.assertThatModels(changeNameRequestModel, changeNameResponse.getCustomer()).match();

        //проверка изменения имени в профиле через GET
        String nameAfterUpdate = UserSteps.getProfileName(reqSpec);
        softly.assertThat(nameForUpdate).isEqualTo(nameAfterUpdate);
        softly.assertThat(nameForUpdate).isNotEqualTo(initialName);
    }


    public static Stream<Arguments> invalidDataForNameUpdating() {
        return Stream.of(
                //Less than two words
                Arguments.of("name"),
                //More than two words
                Arguments.of("One Two Three"),
                //name with more than one space
                Arguments.of("new  name"),
                //name containing digits
                Arguments.of("new name1"),
                //name containing special characters
                Arguments.of("new name!")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDataForNameUpdating")
    public void userCannotChangeNameWithInvalidName(String nameForUpdate) {
        CreateUserRequest userRequest =  AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        String initialName = UserSteps.getProfileName(reqSpec);

        //запрос на изменение и проверка сообщения об ошибке
        new CrudRequester(
                reqSpec,
                Endpoint.UPDATE_PROFILE,
                ResponseSpecs.requestReturnsBadRequest(INVALID_NAME))
                .put(new ChangeNameRequestModel(nameForUpdate));

        //проверка, что имя в профиле не поменялось через GET метод
        String actualName = UserSteps.getProfileName(reqSpec);
        softly.assertThat(initialName).isEqualTo(actualName);
    }
}