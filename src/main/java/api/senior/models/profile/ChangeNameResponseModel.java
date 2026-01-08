package api.senior.models.profile;

import lombok.Data;
import api.middle.iteration2.models.Customer;
import api.senior.models.BaseModel;

@Data
public class ChangeNameResponseModel extends BaseModel {
    private String message;
    private Customer customer;
}