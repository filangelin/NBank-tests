package api.senior.models.transfer;

import lombok.Data;
import api.senior.models.BaseModel;

@Data
public class TransferMoneyResponseModel extends BaseModel {
    private long receiverAccountId;
    private float amount;
    private String message;
    private long senderAccountId;
}