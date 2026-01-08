package api.senior.models.deposit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.senior.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MakeDepositRequestModel extends BaseModel {
    long id;
    private float balance;
}
