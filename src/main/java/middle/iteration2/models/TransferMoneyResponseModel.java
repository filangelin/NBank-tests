package middle.iteration2.models;

import lombok.Data;
import middle.iteration1.models.BaseModel;

@Data
public class TransferMoneyResponseModel extends BaseModel {
    private long receiverAccountId;
    private float amount;
    private String message;
    private long senderAccountId;
}