package api.senior.requests.steps;

import api.database.Condition;
import api.database.DBRequest;
import api.database.mapper.UserRowMapper;
import api.database.mapper.AccountRowMapper;
import api.dao.UserDao;
import api.dao.AccountDao;
import common.helpers.StepLogger;

import java.math.BigDecimal;
import java.util.List;

public class DataBaseSteps {

    public static UserDao getUserByUsername(String username) {
        return StepLogger.log("Get user from database by username: " + username, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("customers")
                        .where(Condition.equalTo("username", username))
                        .extractAs(new UserRowMapper())
        );
    }

    public static UserDao getUserById(Long id) {
        return StepLogger.log("Get user from database by ID: " + id, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("customers")
                        .where(Condition.equalTo("id", id))
                        .extractAs(new UserRowMapper())
        );
    }

    public static UserDao getUserByRole(String role) {
        return StepLogger.log("Get user from database by role: " + role, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("customers")
                        .where(Condition.equalTo("role", role))
                        .extractAs(new UserRowMapper())
        );
    }

    public static AccountDao getAccountByAccountNumber(String accountNumber) {
        return StepLogger.log("Get account from database by account number: " + accountNumber, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("accounts")
                        .where(Condition.equalTo("account_number", accountNumber))
                        .extractAs(new AccountRowMapper())
        );
    }

    public static AccountDao getAccountById(Long id) {
        return StepLogger.log("Get account from database by ID: " + id, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("accounts")
                        .where(Condition.equalTo("id", id))
                        .extractAs(new AccountRowMapper())
        );
    }

    public static AccountDao getAccountByCustomerId(Long customerId) {
        return StepLogger.log("Get account from database by customer ID: " + customerId, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("accounts")
                        .where(Condition.equalTo("customer_id", customerId))
                        .extractAs(new AccountRowMapper())
        );
    }

    public static void updateAccountBalance(Long accountId, Double newBalance) {
        StepLogger.log("Update account balance for account ID: " + accountId + " to: " + newBalance, () -> {

            DBRequest.builder()
                    .requestType(DBRequest.RequestType.UPDATE)
                    .table("accounts")
                    .build()
                    .executeUpdate(
                            "UPDATE accounts SET balance = ? WHERE id = ?",
                            List.of(newBalance, accountId)
                    );

            return null;
        });
    }

    public static BigDecimal getAccountBalanceById(Long accountId) {
        return StepLogger.log("Get account balance by account ID: " + accountId, () -> {
            AccountDao account = getAccountById(accountId);

            if (account == null) {
                throw new RuntimeException("Account not found with ID: " + accountId);
            }

            return account.getBalance();
        });
    }

    public static UserDao getUserByName(String name) {
        return StepLogger.log("Get user from database by name: " + name, () ->
                DBRequest.builder()
                        .requestType(DBRequest.RequestType.SELECT)
                        .table("customers")
                        .where(Condition.equalTo("name", name))
                        .extractAs(new UserRowMapper())
        );
    }
}