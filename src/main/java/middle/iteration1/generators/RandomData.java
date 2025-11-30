package middle.iteration1.generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
    private static final float MIN_AMOUNT = 0.01f;

    private RandomData() {
    }

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                RandomStringUtils.randomNumeric(3) + "$";
    }


    public static float getDepositAmount() {
        final float MAX_DEPOSIT = 5000f;
        return Math.round(
                (MIN_AMOUNT + Math.random() * (MAX_DEPOSIT - MIN_AMOUNT)) * 100
        ) / 100f;
    }


    public static float getTransferAmount() {
        final float MAX_TRANSFER = 10000f;
        return Math.round(
                (MIN_AMOUNT + Math.random() * (MAX_TRANSFER - MIN_AMOUNT)) * 100
        ) / 100f;
    }
}
