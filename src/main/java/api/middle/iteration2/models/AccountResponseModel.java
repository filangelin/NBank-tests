package api.middle.iteration2.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AccountResponseModel {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private List<Transaction> transactions;
}