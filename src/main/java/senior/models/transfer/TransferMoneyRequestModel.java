package senior.models.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import senior.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyRequestModel extends BaseModel {
    private long senderAccountId;
    private long receiverAccountId;
    private float amount;
}