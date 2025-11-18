package middle.iteration2.models;

import lombok.Data;
import middle.iteration1.models.BaseModel;
import middle.iteration1.models.UserRole;

import java.util.List;

@Data
public class GetProfileResponseModel extends BaseModel {
    private Long id;
    private String username;
    private String password;
    private String name;
    private UserRole role;
    private List<AccountResponseModel> accounts;
}
