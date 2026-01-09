package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement depositMoney = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement makeTransfer = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    public static final String DEFAULT_USERNAME = "noname";

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        createNewAccount.click();
        return this;
    }

    public DepositMoneyPage gotoDepositMoney() {
        depositMoney.click();
        return new DepositMoneyPage();
    }

    public TransferPage gotoTransferPage() {
        makeTransfer.click();
        return new TransferPage();
    }

    public UserDashboard checkWelcomeName(String name) {
        welcomeText.shouldHave(Condition.text("Welcome, " + name + "!"));
        return this;
    }
}
