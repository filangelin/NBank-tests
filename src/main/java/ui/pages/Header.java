package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class Header {
    private SelenideElement profileInfo = $(Selectors.byClassName("user-info"));
    private SelenideElement username = $(Selectors.byClassName("user-name"));

    public EditProfilePage gotoEditProfile() {
        profileInfo.click();
        return new EditProfilePage();
    }

    public Header checkUsername(String name) {
        Selenide.refresh(); // тк меняется только после обновления страницы
        username.shouldHave(Condition.exactText((name)));
        return this;
    }

}
