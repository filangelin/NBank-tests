package api.middle.iteration2.models;

import lombok.Data;
import api.middle.iteration1.models.BaseModel;

@Data
public class ChangeNameResponseModel extends BaseModel {
    private String message;
    private Customer customer;
}