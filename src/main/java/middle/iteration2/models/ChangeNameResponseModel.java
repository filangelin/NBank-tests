package middle.iteration2.models;

import lombok.Data;
import middle.iteration1.models.BaseModel;

@Data
public class ChangeNameResponseModel extends BaseModel {
    private String message;
    private Customer customer;
}