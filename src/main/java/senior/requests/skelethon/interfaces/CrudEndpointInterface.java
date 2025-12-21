package senior.requests.skelethon.interfaces;

import senior.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object get();
    Object put(BaseModel model);
    Object update(long id, BaseModel model);
    Object delete(long id);
}
