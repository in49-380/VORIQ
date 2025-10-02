package info.voriq.testing.utils;

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

//    @Step("GET1 {path} [{svc}]")
//    public ValidatableResponse sendGet1Request(String urlPath,
//                                              String path) {
//        Response response = given()
//                .spec(specProvider.apply(svc))
//                .when()
//                .get(path)
//                .then()
//                .statusCode(DEFAULT_STATUS_CODE_POST)
//                .contentType(ContentType.JSON)
//                .log().ifValidationFails()
//                .extract().response();
//        return response.then();
//    }






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