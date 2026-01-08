package api.senior.models;

import lombok.Data;
import api.middle.iteration2.models.Transaction;

import java.util.List;

@Data
public class AccountResponseModel extends BaseModel {
    private Long id;
    private String accountNumber;
    private float balance;
    private List<Transaction> transactions;
}