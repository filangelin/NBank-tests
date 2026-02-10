package api.middle.iteration2.models;

import lombok.Data;
import api.middle.iteration1.models.BaseModel;

import java.math.BigDecimal;

@Data
public class TransferMoneyResponseModel extends BaseModel {
    private long receiverAccountId;
    private BigDecimal amount;
    private String message;
    private long senderAccountId;
}