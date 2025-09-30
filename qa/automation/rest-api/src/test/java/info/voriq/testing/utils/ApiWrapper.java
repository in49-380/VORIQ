package info.voriq.testing.utils;//package info.voriq.testing.utils;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.function.Function;

import static io.restassured.RestAssured.given;


public record ApiWrapper(Function<Service, RequestSpecification> specProvider) {
    private final static int DEFAULT_STATUS_CODE_GET = 200;
    private final static int DEFAULT_STATUS_CODE_POST = 200;

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


    @Step("GET with status code {statusCode} und delay {delay} {path} [{svc}]")
    public ValidatableResponse sendGetRequestWithDelayWithoutBodyStatusCode(Service svc,
                                                                            String path, int delay, int statusCode) {
        Response response = given()
                .spec(specProvider.apply(svc))
                .when()
                .queryParam("delay", delay)
                .get(path)
                .then()
                .statusCode(statusCode)
                .log().ifValidationFails()
                .extract().response();
        return response.then();
    }


    @Step("POST {DEFAULT_STATUS_CODE_POST} {path} [{svc}]")
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

    @Step("POST with status code {statusCode} {path} [{svc}]")
    public ValidatableResponse sendPostRequestWithoutBodyStatusCode(Service svc,
                                                                    String path, int statusCode) {
        Response response = given()
                .spec(specProvider.apply(svc))
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

//import io.qameta.allure.Step;
//import io.restassured.http.ContentType;
//import io.restassured.response.ValidatableResponse;
//import io.restassured.specification.RequestSpecification;
//
//import static io.restassured.RestAssured.given;
//
//public class ApiWrapper  {
//    @Step("GET {path}")
//    public ValidatableResponse getCatalog(String path) {
//        return doGet(catalogSpec(), path);
//    }
//
//    @Step("GET {path} [security]")
//    public ValidatableResponse getSecurity(String path) {
//        return doGet(securitySpec(), path);
//    }
//
//    @Step("POST {path}")
//    public ValidatableResponse postCatalog(String path, Object body) {
//        return doPost(catalogSpec(), path, body);
//    }
//
//    @Step("POST {path} [security]")
//    public ValidatableResponse postSecurity(String path, Object body) {
//        return doPost(securitySpec(), path, body);
//    }
//
//    private ValidatableResponse doGet(RequestSpecification spec, String path) {
//        return given()
//                .spec(spec)
//                .when()
//                .get(path)
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .log().ifValidationFails();
//    }
//
//    private ValidatableResponse doPost(RequestSpecification spec, String path, Object body) {
//        return given()
//                .spec(spec)
//                .body(body)
//                .when()
//                .post(path)
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .log().ifValidationFails();
//    }
//}