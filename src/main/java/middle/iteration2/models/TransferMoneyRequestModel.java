package middle.iteration2.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import middle.iteration1.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyRequestModel extends BaseModel {
    private long senderAccountId;
    private long receiverAccountId;
    private float amount;
}