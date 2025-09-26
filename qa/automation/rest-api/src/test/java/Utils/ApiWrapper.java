package Utils;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.function.Function;

import static io.restassured.RestAssured.given;


public class ApiWrapper {
    private final static int DEFAULT_STATUS_CODE_GET = 200;
    private final static int DEFAULT_STATUS_CODE_POST = 200;
    private final Function<Service, RequestSpecification> specProvider;

    public ApiWrapper(Function<Service, RequestSpecification> specProvider) {
        this.specProvider = specProvider;
    }

    @Step("GET {path} [{svc}]")
    public ValidatableResponse sendGetRequest(Service svc,
                                              String path) {
        Response response = given()
                .spec(specProvider.apply(svc))
                .when()
                .get(path)
                .then()
                .statusCode(DEFAULT_STATUS_CODE_POST)
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .extract().response();
        return response.then();
    }

    @Step("GET {path} [{svc}]")
    public ValidatableResponse sendGetRequestStatusCode(Service svc,
                                                        String path, int statusCode) {
        Response response = given()
                .spec(specProvider.apply(svc))
                .when()
                .get(path)
                .then()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .extract().response();
        return response.then();
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
    @Step("POST 200 {path} [{svc}]")
    public ValidatableResponse sendPostRequest(Service svc,
                                               String path, Object body) {
        Response response = given()
                .spec(specProvider.apply(svc))
                .body(body)
                .when()
                .post(path)
                .then()
                .statusCode(DEFAULT_STATUS_CODE_GET)
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .extract().response();
        return response.then();
    }

    @Step("POST {path} [{svc}]")
    public ValidatableResponse sendPostRequestStatusCode(Service svc,
                                                         String path, Object body, int statusCode) {
        Response response = given()
                .spec(specProvider.apply(svc))
                .body(body)
                .when()
                .post(path)
                .then()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .extract().response();
        return response.then();
    }
}