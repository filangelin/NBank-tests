package api.middle.iteration1.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RandomData {
    private static final BigDecimal MIN_AMOUNT = BigDecimal.valueOf(0.01);

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


    public static BigDecimal getDepositAmount() {
        final BigDecimal MAX_DEPOSIT = BigDecimal.valueOf(5000);

        BigDecimal random = BigDecimal.valueOf(Math.random());

        BigDecimal amount = MIN_AMOUNT.add(
                random.multiply(MAX_DEPOSIT.subtract(MIN_AMOUNT))
        );

        return amount.setScale(2, RoundingMode.HALF_UP);
    }



    public static BigDecimal getTransferAmount() {
        final BigDecimal MAX_TRANSFER = BigDecimal.valueOf(10000);

        BigDecimal random = BigDecimal.valueOf(Math.random());

        BigDecimal amount = MIN_AMOUNT.add(
                random.multiply(MAX_TRANSFER.subtract(MIN_AMOUNT))
        );

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Long getNonexistingAccountId() {
        long nonexistingAccountId;

        do {
            nonexistingAccountId = (long) (Math.random() * 999999) + 1;
            // Здесь будто бы идём в БД и проверяем, существует ли такой ID, если существует, то exists будет равен true и пойдем искать дальше
            boolean exists = false;
            if (!exists) {
                break;
            }

        } while (true);

        return nonexistingAccountId;
    }
}
