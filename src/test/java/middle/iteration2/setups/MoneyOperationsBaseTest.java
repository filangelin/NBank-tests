package middle.iteration2.setups;

import middle.iteration1.models.CreateUserRequest;
import middle.iteration1.requests.CreateAccountRequester;
import middle.iteration1.specs.RequestSpecs;
import middle.iteration1.specs.ResponseSpecs;
import middle.iteration2.models.AccountResponseModel;
import middle.iteration2.models.MakeDepositRequestModel;
import middle.iteration2.requesters.GetAccountsInfo;
import middle.iteration2.requesters.MakeDepositRequester;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class MoneyOperationsBaseTest extends BaseTest {
    protected long user1FirstAccount;
    protected long user1SecondAccount;
    protected long user2FirstAccount;


    @BeforeEach
    public void createAccountsForUsers() {
        user1FirstAccount = createAccount(user1).getId();
        user1SecondAccount = createAccount(user1).getId();
        user2FirstAccount = createAccount(user2).getId();

        //пополнение счета аккаунта, с которого будут осуществлятьсся переводы
        for (int i = 0; i < 3; i++) { // по три раза т.к. максиммум 5k депозита
            //15k для негативного кейса что больше 10к нет возможности для перевода
            new MakeDepositRequester(RequestSpecs.authAsUser(user1.getUsername(), user1.getPassword()),
                    ResponseSpecs.requestReturnsOK())
                    .sendRequest(new MakeDepositRequestModel(user1FirstAccount, 5000));
        }
    }

    private static AccountResponseModel createAccount(CreateUserRequest user) {
        return new CreateAccountRequester(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.entityWasCreated())
                .sendRequest(null).extract().as(AccountResponseModel.class);
    }

    protected static float getCurrentBalance(long accountId, CreateUserRequest user) {
        List<AccountResponseModel> accounts = new GetAccountsInfo(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .sendRequest(null)
                .extract().jsonPath().getList("", AccountResponseModel.class);

        return accounts.stream()
                .filter(it->accountId == it.getId())
                .map(AccountResponseModel::getBalance)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Account with id = %d not found", accountId)));
    }
}