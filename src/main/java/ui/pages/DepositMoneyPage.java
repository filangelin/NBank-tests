package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import java.math.BigDecimal;

import static com.codeborne.selenide.Selenide.$;

public class DepositMoneyPage extends BasePage<DepositMoneyPage> {
    private final SelenideElement selectAccountField = $(".account-selector");
    private final SelenideElement amountField = $(".deposit-input");
    private final SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));
    public final static BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(0.00);
    public final static BigDecimal NEGATIVE_MAXIMUM_BOUNDARY_VALUE = BigDecimal.valueOf(5000.01);

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositMoneyPage selectDepositAccount(Long accountNumber) {
        selectAccountField.click();
        SelenideElement selectedAccount = $(Selectors.byText("ACC" + accountNumber));
        selectedAccount.click();
        selectAccountField.shouldHave(Condition.text(String.valueOf(accountNumber)));
        return this;
    }

    public DepositMoneyPage enterDepositAmount(BigDecimal depositAmount) {
        amountField.sendKeys(String.valueOf(depositAmount));
        return this;
    }

    public UserDashboard clickDepositButton() {
        depositButton.click();
        return new UserDashboard();
    }

    public DepositMoneyPage checkAccountBalance(Long accountNumber, BigDecimal depositAmount) {
        selectDepositAccount(accountNumber);
        selectAccountField.shouldHave(Condition.text(String.valueOf(depositAmount)));
        return this;
    }

}
