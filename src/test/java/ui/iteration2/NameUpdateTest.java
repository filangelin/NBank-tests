package ui.iteration2;

import api.senior.requests.steps.UserSteps;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static ui.pages.BasePage.authAsUser;
import static ui.pages.UserDashboard.DEFAULT_USERNAME;


public class NameUpdateTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanChangeNameWithValidName() {
        var user = SessionStorage.getUser();
        Name fakerName = new Faker().name();
        String nameForUpdate = fakerName.firstName() + " " + fakerName.lastName();

        new UserDashboard().open()
                .checkWelcomeName(DEFAULT_USERNAME)
                .header.checkUsername(DEFAULT_USERNAME)
                .gotoEditProfile()
                .changeName(nameForUpdate)
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_CHANGED_SUCCESSFULLY.getMessage())
                .goToHomePage()
                .checkWelcomeName(nameForUpdate)
                .header.checkUsername(nameForUpdate);

        String nameAfterUpdate = new UserSteps(user.getUsername(), user.getPassword())
                .getProfileName();
        assertThat(nameForUpdate).isEqualTo(nameAfterUpdate);
    }

    @Test
    @UserSession
    public void userCannotChangeNameWithInvalidName() {
        var user = SessionStorage.getUser();

        new UserDashboard().open()
                .header.gotoEditProfile()
                .changeName("name")
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage())
                .goToHomePage()
                .checkWelcomeName(DEFAULT_USERNAME)
                .header.checkUsername(DEFAULT_USERNAME);;

        String nameAfterUpdate = new UserSteps(user.getUsername(), user.getPassword())
                .getProfileName();
        assertThat(nameAfterUpdate).isEqualTo(null);
    }

    @Test
    @UserSession
    public void userCannotChangeNameWithEmptyName() {
        var user = SessionStorage.getUser();

        new UserDashboard().open()
                .header.gotoEditProfile()
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.INVALID_NAME.getMessage())
                .goToHomePage()
                .checkWelcomeName(DEFAULT_USERNAME)
                .header.checkUsername(DEFAULT_USERNAME);;

        String nameAfterUpdate = new UserSteps(user.getUsername(), user.getPassword())
                .getProfileName();
        assertThat(nameAfterUpdate).isEqualTo(null);
    }
}