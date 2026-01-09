package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    NAME_CHANGED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    INVALID_NAME("❌ Please enter a valid name."),
    INVALID_DEPOSIT("❌ Please enter a valid amount."),
    UNSELECTED_ACCOUNT("❌ Please select an account."),
    DEPOSIT_MUST_BE_LESS_OR_EQUAL("❌ Please deposit less or equal to 5000$."),
    DEPOSIT_SUCCESSFULLY("✅ Successfully deposited "),
    TRANSFER_SUCCESSFULLY("✅ Successfully transferred"),
    NO_USER_FOUND("❌ No user found with this account number."),
    FILL_ALL_TRANSFER_FIELDS("❌ Please fill all fields and confirm.");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}
