package senior.models.profile;

import lombok.Data;
import middle.iteration2.models.Customer;
import senior.models.BaseModel;

@Data
public class ChangeNameResponseModel extends BaseModel {
    private String message;
    private Customer customer;
}