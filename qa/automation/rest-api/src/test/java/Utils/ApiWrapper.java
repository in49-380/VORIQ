package Utils;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.function.Function;

import static io.restassured.RestAssured.given;


public class ApiWrapper {
    private final static int DEFAULT_STATUS_CODE_GET = 200;
    private final Function<Service, RequestSpecification> specProvider;

    public ApiWrapper(Function<Service, RequestSpecification> specProvider) {
        this.specProvider = specProvider;
    }

    @Step("GET {path} [{svc}]")
    public  ValidatableResponse sendGetRequest(Service svc,
                                                     String callPath) {
       return given()
                .spec(specProvider.apply(svc))
                .when()
                .get(callPath)
                .then();
    }

//    public static ValidatableResponse sendGetRequest(String callPath, int statusCode) {
//        return sendGetRequest(given(), callPath, statusCode);
//    }
//
//    public static ValidatableResponse sendGetRequest(RequestSpecification requestSpecification, String callPath) {
//        return sendGetRequest(requestSpecification, callPath, DEFAULT_STATUS_CODE_GET);
//    }
//
//    public static ValidatableResponse sendGetRequest(String callPath) {
//        return sendGetRequest(given(), callPath, DEFAULT_STATUS_CODE_GET);
//    }
@Step("Post {callPath} [{svc}]")
public  ValidatableResponse sendPostRequest(Service svc,
                                           String callPath, Object body) {
    return given()
            .spec(specProvider.apply(svc))
            .body(body)
            .when()
            .post(callPath)
            .then();
}



}