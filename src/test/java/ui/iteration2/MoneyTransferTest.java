package ui.iteration2;

import api.senior.models.CreateUserRequest;
import api.senior.requests.steps.AdminSteps;
import api.senior.requests.steps.UserSteps;
import api.senior.specs.RequestSpecs;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import static api.middle.iteration1.generators.RandomData.getNonexistingAccountId;
import static api.middle.iteration1.generators.RandomData.getTransferAmount;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ui.pages.BasePage.authAsUser;

public class MoneyTransferTest extends BaseUiTest {

    @Test
    public void userCanTransferMoney() {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        authAsUser(userRequest1);
        float amount = getTransferAmount();
        //пополнение баланса
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountId1)
                .inputRecipientName(userRequest2.getUsername())
                .selectRecipientAccount(accountId2)
                .inputTransferAmount(amount)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_SUCCESSFULLY.getMessage())
                .refresh()
                .selectAccount(accountId1)
                .checkAccountBalance(0.00f);

        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isZero();
        softly.assertThat(receiverUpdatedBalance).isEqualTo(amount);
    }


    @Test
    public void userCanTransferMoneyBetweenOwnAccounts() {
        // создание пользователя и его аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec1).getId();

        authAsUser(userRequest1);

        // пополнение баланса
        float amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId2);

        float senderExpectedBalance = senderInitialBalance - amount;
        float receiverExpectedBalance = receiverInitialBalance + amount;

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountId1)
                .inputRecipientName(userRequest1.getUsername())
                .selectRecipientAccount(accountId2)
                .inputTransferAmount(amount)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_SUCCESSFULLY.getMessage())
                .refresh()
                .selectAccount(accountId1)
                .checkAccountBalance(senderExpectedBalance)
                .selectAccount(accountId2)
                .checkAccountBalance(receiverExpectedBalance);

        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderExpectedBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverExpectedBalance);
    }


    @Test
    public void userCannotTransferWithEmptyMoneyAmount() {
        //cоздание пользователей и аккаунтов
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        var reqSpec2 = RequestSpecs.authAsUser(userRequest2.getUsername(), userRequest2.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();
        Long accountId2 = UserSteps.createAccount(reqSpec2).getId();
        float amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec1, accountId1, amount);
        authAsUser(userRequest1);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountId1)
                .inputRecipientName(userRequest2.getUsername())
                .selectRecipientAccount(accountId2)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.FILL_ALL_TRANSFER_FIELDS.getMessage())
                .refresh()
                .selectAccount(accountId1)
                .checkAccountBalance(amount);


        //проверка, что балансы отправителя и получателя не изменились
        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        float receiverUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec2, accountId2);
        softly.assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
        softly.assertThat(receiverUpdatedBalance).isEqualTo(receiverInitialBalance);
    }


    @Test
    public void userCannotTransferToNonExistingAccount() {
        // создание пользователя и аккаунта
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var reqSpec1 = RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword());
        Long accountId1 = UserSteps.createAccount(reqSpec1).getId();

        float amount = getTransferAmount();
        UserSteps.depositMoney(reqSpec1, accountId1, amount);

        authAsUser(userRequest1);

        float senderInitialBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);

        Long nonExistingAccountId = getNonexistingAccountId();

        new UserDashboard().open()
                .gotoTransferPage()
                .selectAccount(accountId1)
                .inputRecipientName("non_existing_user")
                .selectRecipientAccount(nonExistingAccountId)
                .inputTransferAmount(amount)
                .confirmDetailsAreCorrect()
                .clickTransferButton()
                .checkAlertMessageAndAccept(BankAlert.NO_USER_FOUND.getMessage())
                .refresh()
                .selectAccount(accountId1)
                .checkAccountBalance(senderInitialBalance);

        // проверка, что баланс не изменился
        float senderUpdatedBalance = UserSteps.getCurrentAccountBalance(reqSpec1, accountId1);
        assertThat(senderUpdatedBalance).isEqualTo(senderInitialBalance);
    }
}