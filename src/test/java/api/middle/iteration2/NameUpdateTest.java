package api.middle.iteration2;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import api.middle.iteration1.specs.RequestSpecs;
import api.middle.iteration1.specs.ResponseSpecs;
import api.middle.iteration2.models.ChangeNameRequestModel;
import api.middle.iteration2.models.ChangeNameResponseModel;
import api.middle.iteration2.requesters.ChangeNameRequester;
import api.middle.iteration2.requesters.GetProfileRequester;
import api.middle.iteration2.setups.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


public class NameUpdateTest extends BaseTest {

    @Test
    public void userCanChangeNameWithValidName() {
        String initialName = getCurrentName();

        Name fakerName = new Faker().name();
        String nameForUpdate = fakerName.firstName() + " " + fakerName.lastName();

        //запрос на изменение
        ChangeNameResponseModel changeNameResponse = new ChangeNameRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .sendRequest(new ChangeNameRequestModel(nameForUpdate))
                .extract().as(ChangeNameResponseModel.class);

        //проерка ответа
        softly.assertThat(nameForUpdate).isEqualTo(changeNameResponse.getCustomer().getName());
        softly.assertThat("Profile updated successfully").isEqualTo(changeNameResponse.getMessage());


        //проверка изменения имени в профиле через GET
        String nameAfterUpdate = getCurrentName();

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
        String initialName = getCurrentName();

        //запрос на изменение и проверка сообщения об ошибке
        new ChangeNameRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsBadRequest("Name must contain two words with letters only"))
                .sendRequest(new ChangeNameRequestModel(nameForUpdate));


        //проверка, что имя в профиле не поменялось через GET метод
        String actualName = getCurrentName();
        softly.assertThat(initialName).isEqualTo(actualName);
    }


    private String getCurrentName() {
        return new GetProfileRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .sendRequest(null).extract().path("name");
    }
}