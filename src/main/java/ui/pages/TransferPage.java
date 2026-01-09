package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage<TransferPage> {

    private SelenideElement selectAccountField = $(".account-selector");
    private SelenideElement recipientNameField = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recipientAccountNumberField = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement amountField = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement confirmDetailsCheckbox = $("#confirmCheck");
    private SelenideElement sendTransferButton = $(byText("\uD83D\uDE80 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage selectAccount(Long accountNumber) {
        selectAccountField.click();
        SelenideElement selectAccount = $(Selectors.byText("ACC" + accountNumber));
        selectAccount.click();
        selectAccountField.shouldHave(Condition.text(String.valueOf(accountNumber)));
        return this;
    }

    public TransferPage inputRecipientName(String userName) {
        recipientNameField.sendKeys(userName);
        return this;
    }

    public TransferPage selectRecipientAccount(Long accountNumber) {
        recipientAccountNumberField.sendKeys("ACC" + accountNumber);
        return this;
    }

    public TransferPage inputTransferAmount(float transferAmount) {
        amountField.sendKeys(String.valueOf(transferAmount));
        return this;
    }

    public TransferPage confirmDetailsAreCorrect() {
        confirmDetailsCheckbox.click();
        return this;
    }

    public TransferPage clickTransferButton() {
        sendTransferButton.click();
        return this;
    }

    public TransferPage checkAccountBalance(float balance) {
        String expectedBalance = String.format("%.2f", balance).replace(',', '.');
        selectAccountField.shouldHave(Condition.text(expectedBalance));
        return this;
    }
}
