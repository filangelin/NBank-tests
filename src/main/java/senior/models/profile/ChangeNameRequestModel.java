package senior.models.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import senior.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeNameRequestModel extends BaseModel {
    private String name;
}