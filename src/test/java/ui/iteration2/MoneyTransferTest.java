package ui.iteration2;

import common.annotations.Account;
import common.annotations.AccountSpec;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

import static api.middle.iteration1.generators.RandomData.getNonexistingAccountId;
import static api.middle.iteration1.generators.RandomData.getTransferAmount;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MoneyTransferTest extends BaseUiTest {

    @Test
    @UserSession(value = 2)
    @Account({@AccountSpec, @AccountSpec(user = 2)})
    public void userCanTransferMoney() {
        var recipient = SessionStorage.getUser(2);
        Long accountId1 = SessionStorage.getAccount().getId();
        String accountNumber =  SessionStorage.getAccount().getAccountNumber();
        Long accountId2 = SessionStorage.getAccount(2).getId();
        String accountNumber2 = SessionStorage.getAccount(2).getAccountNumber();
        BigDecimal amount = getTransferAmount();
        SessionStorage.getSteps().depositMoney(accountId1, amount);

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountNumber)
                .inputRecipientName(recipient.getUsername())
                .selectRecipientAccount(accountNumber2)
                .inputTransferAmount(amount)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_SUCCESSFULLY.getMessage()) //Expecting actual:
                .refresh()
                .selectAccount(accountNumber)
                .checkAccountBalance(BigDecimal.valueOf(0.00));

        BigDecimal senderUpdatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);
        BigDecimal receiverUpdatedBalance = SessionStorage.getSteps(2).getCurrentAccountBalance(accountId2);
        softly.assertThat(senderUpdatedBalance).isZero();
        softly.assertThat(receiverUpdatedBalance).isEqualTo(amount);
    }


    @Test
    @UserSession
    @Account({@AccountSpec(count = 2)})
    public void userCanTransferMoneyBetweenOwnAccounts() {
        var user = SessionStorage.getUser();
        Long accountId1 = SessionStorage.getAccount().getId();
        String accountNumber =  SessionStorage.getAccount().getAccountNumber();
        Long accountId2 = SessionStorage.getAccount(1, 2).getId();
        String accountNumber2 =  SessionStorage.getAccount(1, 2).getAccountNumber();
        BigDecimal amount = getTransferAmount();
        SessionStorage.getSteps().depositMoney(accountId1, amount);

        BigDecimal senderInitialBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);
        BigDecimal receiverInitialBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId2);

        BigDecimal senderExpectedBalance = senderInitialBalance.subtract(amount);
        BigDecimal receiverExpectedBalance = receiverInitialBalance.add(amount);

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountNumber)
                .inputRecipientName(user.getUsername())
                .selectRecipientAccount(accountNumber2)
                .inputTransferAmount(amount)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_SUCCESSFULLY.getMessage()) // TODO flaky
                .refresh()
                .selectAccount(accountNumber)
                .checkAccountBalance(senderExpectedBalance)
                .selectAccount(accountNumber2)
                .checkAccountBalance(receiverExpectedBalance);

        BigDecimal senderUpdatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);
        BigDecimal receiverUpdatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualByComparingTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualByComparingTo(receiverExpectedBalance);
    }


    @Test
    @UserSession(value = 2)
    @Account({@AccountSpec, @AccountSpec(user = 2)})
    public void userCannotTransferWithEmptyMoneyAmount() {
        var recipient = SessionStorage.getUser(2);
        Long accountId1 = SessionStorage.getAccount().getId();
        String accountNumber =  SessionStorage.getAccount().getAccountNumber();
        Long accountId2 = SessionStorage.getAccount(2).getId();
        String accountNumber2 =  SessionStorage.getAccount(2).getAccountNumber();
        BigDecimal amount = getTransferAmount();
        SessionStorage.getSteps().depositMoney(accountId1, amount);

        BigDecimal senderInitialBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);
        BigDecimal receiverInitialBalance = SessionStorage.getSteps(2).getCurrentAccountBalance(accountId2);

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountNumber) //не находит элемент, так как появляется небыстро
                .inputRecipientName(recipient.getUsername())
                .selectRecipientAccount(accountNumber2)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.FILL_ALL_TRANSFER_FIELDS.getMessage())
                .refresh()
                .selectAccount(accountNumber)
                .checkAccountBalance(amount);


        //проверка, что балансы отправителя и получателя не изменились
        BigDecimal senderUpdatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);
        BigDecimal receiverUpdatedBalance = SessionStorage.getSteps(2).getCurrentAccountBalance(accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    @UserSession()
    @Account({@AccountSpec})
    public void userCannotTransferToNonExistingAccount() {
        Long accountId1 = SessionStorage.getAccount().getId();
        String accountNumber =  SessionStorage.getAccount().getAccountNumber();
        BigDecimal amount = getTransferAmount();
        SessionStorage.getSteps().depositMoney(accountId1, amount);

        BigDecimal senderInitialBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);

        String nonExistingAccountId = getNonexistingAccountId().toString();

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountNumber)
                .inputRecipientName("non_existing_user")
                .selectRecipientAccount(nonExistingAccountId)
                .inputTransferAmount(amount)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.NO_USER_FOUND.getMessage())
                .refresh()
                .selectAccount(accountNumber)
                .checkAccountBalance(senderInitialBalance);

        // проверка, что баланс не изменился
        BigDecimal senderUpdatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId1);
        assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
    }
}