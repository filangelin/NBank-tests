package senior.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Errors {
    INVALID_NAME("Name must contain two words with letters only"),
    LEAST_TRANSFER("Transfer amount must be at least 0.01"),
    EXCEEDED_TRANSFER("Transfer amount cannot exceed 10000"),
    INSUFFICIENT_FUND("insufficient funds or invalid accounts"),
    EXCEEDED_DEPOSIT("Deposit amount cannot exceed 5000"),
    LEAST_DEPOSIT("Deposit amount must be at least 0.01");

    private final String msg;
}
