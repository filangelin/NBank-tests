package middle.iteration2.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import middle.iteration1.requests.Request;
import middle.iteration2.Endpoints;
import middle.iteration2.models.ChangeNameRequestModel;

import static io.restassured.RestAssured.given;

public class ChangeNameRequester extends Request<ChangeNameRequestModel> {

    public ChangeNameRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse sendRequest(ChangeNameRequestModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .put(Endpoints.PROFILE)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}