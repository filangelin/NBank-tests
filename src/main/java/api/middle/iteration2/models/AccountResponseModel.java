package api.middle.iteration2.models;

import lombok.Data;

import java.util.List;

@Data
public class AccountResponseModel {
    private Long id;
    private String accountNumber;
    private float balance;
    private List<Transaction> transactions;
}