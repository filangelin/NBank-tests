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

import static api.middle.iteration1.generators.RandomData.getDepositAmount;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ui.pages.DepositMoneyPage.DEFAULT_BALANCE;
import static ui.pages.DepositMoneyPage.NEGATIVE_MAXIMUM_BOUNDARY_VALUE;


public class DepositTest extends BaseUiTest {

    @Test
    @UserSession
    @Account({@AccountSpec})
    public void userCanDeposit() {
        Long accountId = SessionStorage.getAccount().getId();
        var depositAmount = getDepositAmount();

        new UserDashboard().open()
                .gotoDepositMoney()
                .selectDepositAccount(accountId)
                .enterDepositAmount(depositAmount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_SUCCESSFULLY.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, depositAmount);

        BigDecimal updatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId);
        assertThat(updatedBalance).isEqualTo(depositAmount);
    }

    @Test
    @UserSession
    @Account({@AccountSpec})
    public void userCannotDepositMoreThanMaximumDeposit() {
        Long accountId = SessionStorage.getAccount().getId();

        new UserDashboard().open()
                .gotoDepositMoney()
                .selectDepositAccount(accountId)
                .enterDepositAmount(NEGATIVE_MAXIMUM_BOUNDARY_VALUE)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MUST_BE_LESS_OR_EQUAL.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, DEFAULT_BALANCE);

        BigDecimal updatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId);
        assertThat(updatedBalance).isZero();
    }

    @Test
    @UserSession
    @Account({@AccountSpec})
    public void userCannotDepositWithInvalidDepositAmount() {
        Long accountId = SessionStorage.getAccount().getId();

        new UserDashboard().open()
                .gotoDepositMoney()
                .selectDepositAccount(accountId)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.INVALID_DEPOSIT.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, DEFAULT_BALANCE);

        BigDecimal updatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId);
        assertThat(updatedBalance).isZero();
    }

    @Test
    @UserSession
    @Account({@AccountSpec})
    public void userCannotDepositWithUnselectedAccount() {
        Long accountId = SessionStorage.getAccount().getId();
        var depositAmount = getDepositAmount();

        new UserDashboard().open()
                .gotoDepositMoney()
                .enterDepositAmount(depositAmount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.UNSELECTED_ACCOUNT.getMessage())
                .gotoDepositMoney()
                .checkAccountBalance(accountId, DEFAULT_BALANCE);

        BigDecimal updatedBalance = SessionStorage.getSteps().getCurrentAccountBalance(accountId);
        assertThat(updatedBalance).isZero();
    }
}