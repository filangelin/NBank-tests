package common.extensions;

import common.annotations.Account;
import common.annotations.AccountSpec;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AccountExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Account accountsAnnotation =
                context.getRequiredTestMethod().getAnnotation(Account.class);

        if (accountsAnnotation == null) {
            return;
        }

        for (AccountSpec spec : accountsAnnotation.value()) {
            int userIndex = spec.user();
            int count = spec.count();

            for (int i = 0; i < count; i++) {
                var account = SessionStorage.getSteps(userIndex).createAccount();

                SessionStorage.addAccount(userIndex, account);
            }
        }
    }
}
