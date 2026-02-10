package api.middle.iteration2.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.middle.iteration1.models.BaseModel;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MakeDepositRequestModel extends BaseModel {
    long id;
    private BigDecimal balance;
}
