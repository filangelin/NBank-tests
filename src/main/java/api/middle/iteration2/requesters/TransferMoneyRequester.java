package api.middle.iteration2.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.middle.iteration1.requests.Request;
import api.middle.iteration2.Endpoints;
import api.middle.iteration2.models.TransferMoneyRequestModel;

import static io.restassured.RestAssured.given;

public class TransferMoneyRequester extends Request<TransferMoneyRequestModel> {

    public TransferMoneyRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse sendRequest(TransferMoneyRequestModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post(Endpoints.TRANSFER)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}