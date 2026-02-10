package api.senior.models.transfer;

import lombok.Data;
import api.senior.models.BaseModel;

import java.math.BigDecimal;

@Data
public class TransferMoneyResponseModel extends BaseModel {
    private long receiverAccountId;
    private BigDecimal amount;
    private String message;
    private long senderAccountId;
}