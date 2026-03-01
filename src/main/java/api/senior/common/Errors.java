package api.senior.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Errors {
    INVALID_NAME("Name must contain two words with letters only"),
    LEAST_TRANSFER("Invalid transfer: insufficient funds or invalid accounts"),
    EXCEEDED_TRANSFER("Transfer amount cannot exceed 10000"),
    INSUFFICIENT_FUND("insufficient funds or invalid accounts"),
    EXCEEDED_DEPOSIT("Deposit amount exceeds the 5000 limit"),
    LEAST_DEPOSIT("Invalid account or amount");

    private final String msg;
}
