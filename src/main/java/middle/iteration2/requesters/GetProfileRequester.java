package middle.iteration2.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import middle.iteration1.models.BaseModel;
import middle.iteration1.requests.Request;
import middle.iteration2.Endpoints;

import static io.restassured.RestAssured.given;

public class GetProfileRequester extends Request {
    public GetProfileRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse sendRequest(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .get(Endpoints.PROFILE)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
