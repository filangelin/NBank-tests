package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private SelenideElement nameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveChangesButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));
    private SelenideElement home = $(Selectors.byText("\uD83C\uDFE0 Home"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage changeName(String newName) {
        RetryUtils.retry(() -> nameInput.setValue(newName),
                1000,
                result -> Objects.equals(result.getAttribute("value"), newName),
                3,
                2000
        );
        return this;
    }

    public EditProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

    public UserDashboard goToHomePage() {
        home.click();
        return new UserDashboard();
    }
}
