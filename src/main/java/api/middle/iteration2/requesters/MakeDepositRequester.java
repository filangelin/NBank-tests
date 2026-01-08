package api.middle.iteration2.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.middle.iteration1.requests.Request;
import api.middle.iteration2.Endpoints;
import api.middle.iteration2.models.MakeDepositRequestModel;

import static io.restassured.RestAssured.given;

public class MakeDepositRequester extends Request<MakeDepositRequestModel> {
    public MakeDepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse sendRequest(MakeDepositRequestModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post(Endpoints.DEPOSIT)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
