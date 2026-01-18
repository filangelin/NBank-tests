package ui.iteration2;

import api.senior.models.CreateUserRequest;
import api.senior.requests.steps.AdminSteps;
import api.senior.requests.steps.UserSteps;
import api.senior.specs.RequestSpecs;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import static api.middle.iteration1.generators.RandomData.getDepositAmount;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ui.pages.DepositMoneyPage.DEFAULT_BALANCE;
import static ui.pages.DepositMoneyPage.NEGATIVE_MAXIMUM_BOUNDARY_VALUE;


public class DepositTest extends BaseUiTest {

    @Test
    public void userCanDeposit() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        authAsUser(userRequest);
        Long accountId = UserSteps.createAccount(reqSpec).getId();
        var depositAmount = getDepositAmount();

        new UserDashboard().open()
                .gotoDepositMoney()
                .selectDepositAccount(accountId)
                .enterDepositAmount(depositAmount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_SUCCESSFULLY.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, depositAmount);

        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        assertThat(updatedBalance).isEqualTo(depositAmount);
    }

    @Test
    public void userCannotDepositMoreThanMaximumDeposit() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        authAsUser(userRequest);
        Long accountId = UserSteps.createAccount(reqSpec).getId();

        new UserDashboard().open()
                .gotoDepositMoney()
                .selectDepositAccount(accountId)
                .enterDepositAmount(NEGATIVE_MAXIMUM_BOUNDARY_VALUE)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MUST_BE_LESS_OR_EQUAL.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, DEFAULT_BALANCE);

        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        assertThat(updatedBalance).isZero();
    }

    @Test
    public void userCannotDepositWithInvalidDepositAmount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        authAsUser(userRequest);
        Long accountId = UserSteps.createAccount(reqSpec).getId();

        new UserDashboard().open()
                .gotoDepositMoney()
                .selectDepositAccount(accountId)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.INVALID_DEPOSIT.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, DEFAULT_BALANCE);

        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        assertThat(updatedBalance).isZero();
    }

    @Test
    public void userCannotDepositWithUnselectedAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var reqSpec = RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
        authAsUser(userRequest);
        Long accountId = UserSteps.createAccount(reqSpec).getId();
        var depositAmount = getDepositAmount();

        new UserDashboard().open()
                .gotoDepositMoney()
                .enterDepositAmount(depositAmount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.UNSELECTED_ACCOUNT.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, DEFAULT_BALANCE);

        float updatedBalance = UserSteps.getCurrentAccountBalance(reqSpec, accountId);
        assertThat(updatedBalance).isZero();
    }
}