package info.voriq.testing.utils;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.function.Function;

import static io.restassured.RestAssured.given;

public record ApiWrapper(Function<Service, RequestSpecification> specProvider) {

    private static final int DEFAULT_STATUS_CODE_GET = 200;
    private static final int DEFAULT_STATUS_CODE_POST = 200;


    private ValidatableResponse request(
            Service svc,
            String method,
            String path,
            Object body,
            Map<String, ?> queryParams,
            int delay,
            int expectedStatus,
            ContentType expectedContentType
    ) {
        RequestSpecification spec = given().spec(specProvider.apply(svc));

        if (queryParams != null && !queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }

        if (delay != 0) {
            spec.queryParam("delay", delay);
        }

        if (body != null) {
            spec.body(body);
        }

        Response resp = switch (method) {
            case "GET" -> spec.when().get(path);
            case "POST" -> spec.when().post(path);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };

        ValidatableResponse then = resp.then()
                .statusCode(expectedStatus)
                .contentType(ContentType.JSON)
                .log().ifValidationFails();

        if (expectedContentType != null) {
            then.contentType(expectedContentType);
        }
        return then;
    }

    // ---- GET ----
    @Step("GET {path} [{svc}] → ожидаем {expectedStatus}")
    public ValidatableResponse get(Service svc, String path, int expectedStatus) {
        return request(svc, "GET", path, null, null, 0, expectedStatus, ContentType.JSON);
    }


    @Step("GET {path} [{svc}]")
    public ValidatableResponse get(Service svc, String path) {
        return get(svc, path, DEFAULT_STATUS_CODE_GET);
    }

    @Step("GET {path} [{svc}] c query={queryParams} → ожидаем {expectedStatus}")
    public ValidatableResponse get(Service svc, String path, Map<String, ?> queryParams, int expectedStatus) {
        return request(svc, "GET", path, null, queryParams, 0, expectedStatus, ContentType.JSON);
    }


    @Step("GET {path} [{svc}] c delay={delay} → ожидаем {expectedStatus}")
    public ValidatableResponse get(Service svc, String path, int delay, int expectedStatus) {
        return request(svc, "GET", path, null, null, delay, expectedStatus, ContentType.JSON);
    }

    // ---- POST ----
    @Step("POST {path} [{svc}] без тела → ожидаем {expectedStatus}")
    public ValidatableResponse post(Service svc, String path, int expectedStatus) {
        return request(svc, "POST", path, null, null, 0, expectedStatus, ContentType.JSON);
    }

    @Step("POST {path} [{svc}] body={body} → ожидаем {expectedStatus}")
    public ValidatableResponse post(Service svc, String path, Object body, int expectedStatus) {
        return request(svc, "POST", path, body, null, 0, expectedStatus, ContentType.JSON);
    }

    @Step("POST {path} [{svc}] body={body}→ ожидаем {DEFAULT_STATUS_CODE_POST}")
    public ValidatableResponse post(Service svc, String path, Object body) {
        return post(svc, path, body, DEFAULT_STATUS_CODE_POST);
    }
}