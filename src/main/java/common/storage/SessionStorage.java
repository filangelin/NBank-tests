package common.storage;

import api.senior.models.AccountResponseModel;
import api.senior.models.CreateUserRequest;
import api.senior.requests.steps.UserSteps;

import java.util.*;

public class SessionStorage {
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final HashMap<Integer, List<AccountResponseModel>> usersAccounts = new HashMap<>();

    private SessionStorage() {
    }

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    /**
     * Возвращаем объект CreateUserRequest по его порядковому номеру в списке созданных пользователей.
     *
     * @param number Порядковый номер, начиная с 1 (а не с 0).
     * @return Объект CreateUserRequest, соответствующий указанному порядковому номеру.
     */
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.keySet()).get(number - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.values()).get(number - 1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear() {
        INSTANCE.get().userStepsMap.clear();
        INSTANCE.get().usersAccounts.clear();
    }

    public static void addAccount(int userIndex, AccountResponseModel account) {
        var accountsList = INSTANCE.get().usersAccounts.computeIfAbsent(userIndex, k -> new LinkedList<>());
        accountsList.add(account);
    }

    public static AccountResponseModel getAccount(int userIndex) {
        return INSTANCE.get().usersAccounts.get(userIndex).getFirst();
    }

    public static AccountResponseModel getAccount(int userIndex, int accountIndex) {
        return INSTANCE.get().usersAccounts.get(userIndex).get(accountIndex - 1);
    }

    public static AccountResponseModel getAccount() {
        return INSTANCE.get().usersAccounts.get(1).getFirst();
    }
}
