package api.senior.models.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.senior.models.BaseModel;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyRequestModel extends BaseModel {
    private long senderAccountId;
    private long receiverAccountId;
    private BigDecimal amount;
}