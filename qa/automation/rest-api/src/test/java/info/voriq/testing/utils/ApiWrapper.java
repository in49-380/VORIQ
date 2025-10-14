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

    public enum AuthMode {AUTH, NO_AUTH}

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
            ContentType expectedContentType,
            CtCheckMode ctMode,
            AuthMode authMode

    ) {
        RequestSpecification spec = given().spec(specProvider.apply(svc));

        if (authMode == AuthMode.AUTH) {
            // добавляем авторизацию только когда нужно
            spec.header("Authorization", "Bearer " + info.voriq.testing.config.ConfigManager.token());
        }

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
                .log().ifValidationFails();

        if (expectedContentType != null && ctMode != CtCheckMode.NONE) {
            String ctHeader = resp.getHeader("Content-Type");

            if (ctMode == CtCheckMode.REQUIRE) {
                if (ctHeader == null || ctHeader.isBlank()) {
                    throw new AssertionError("Expected Content-Type header=" + expectedContentType + ", but it is not present.");
                }
                then.contentType(expectedContentType);
            } else { // IF_PRESENT
                if (ctHeader != null && !ctHeader.isBlank()) {
                    then.contentType(expectedContentType);
                }
            }
        }
        return then;
    }

    //---------------- GET (auth by default) -----------
    @Step("GET {path} [{svc}] → ожидаем {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse get(Service svc, String path, int expectedStatus) {
        return request(svc, "GET", path, null, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.AUTH);
    }


    @Step("GET {path} [{svc}]")
    public ValidatableResponse get(Service svc, String path) {
        return get(svc, path, DEFAULT_STATUS_CODE_GET);
    }

    @Step("GET {path} [{svc}] with query={queryParams} → expected {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse get(Service svc, String path, Map<String, ?> queryParams, int expectedStatus) {
        return request(svc, "GET", path, null, queryParams, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.AUTH);
    }

    @Step("GET {path} [{svc}] with delay={delay} → expected {expectedStatus} (NO CT CHECK)")
    public ValidatableResponse get(Service svc, String path, int delay, int expectedStatus) {
        return request(svc, "GET", path, null, null, delay, expectedStatus, null, CtCheckMode.NONE, AuthMode.AUTH);
    }

    @Step("GET {path} [{svc}] → expected {expectedStatus} (JSON, IF_PRESENT)")
    public ValidatableResponse getIfPresent(Service svc, String path, int expectedStatus) {
        return request(svc, "GET", path, null, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.IF_PRESENT, AuthMode.AUTH);
    }

    // ---------------- GET (NO AUTH) ----------------
    @Step("GET {path} [{svc}] (NO AUTH) → ожидаем {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse getNoAuth(Service svc, String path, int expectedStatus) {
        return request(svc, "GET", path, null, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.NO_AUTH);
    }

    @Step("GET {path} [{svc}] (NO AUTH)")
    public ValidatableResponse getNoAuth(Service svc, String path) {
        return getNoAuth(svc, path, DEFAULT_STATUS_CODE_GET);
    }

    @Step("GET {path} [{svc}] (NO AUTH) with query={queryParams} → expected {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse getNoAuth(Service svc, String path, Map<String, ?> queryParams, int expectedStatus) {
        return request(svc, "GET", path, null, queryParams, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.NO_AUTH);
    }

    // ---------------- POST (auth by default) ----------------
    @Step("POST {path} [{svc}] without body → expected {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse post(Service svc, String path, int expectedStatus) {
        return request(svc, "POST", path, null, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.AUTH);
    }

    @Step("POST {path} [{svc}] body={body} → expected {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse post(Service svc, String path, Object body, int expectedStatus) {
        return request(svc, "POST", path, body, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.AUTH);
    }

    @Step("POST {path} [{svc}] body={body}→ expected 200 (JSON, STRICT)")
    public ValidatableResponse post(Service svc, String path, Object body) {
        return post(svc, path, body, DEFAULT_STATUS_CODE_POST);
    }

    // ---------------- POST (NO AUTH) ----------------
    @Step("POST {path} [{svc}] (NO AUTH) without body → expected {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse postNoAuth(Service svc, String path, int expectedStatus) {
        return request(svc, "POST", path, null, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.NO_AUTH);
    }

    @Step("POST {path} [{svc}] (NO AUTH) body={body} → expected {expectedStatus} (JSON, STRICT)")
    public ValidatableResponse postNoAuth(Service svc, String path, Object body, int expectedStatus) {
        return request(svc, "POST", path, body, null, 0, expectedStatus, ContentType.JSON, CtCheckMode.REQUIRE, AuthMode.NO_AUTH);
    }

    @Step("POST {path} [{svc}] (NO AUTH) body={body} → expected 200 (JSON, STRICT)")
    public ValidatableResponse postNoAuth(Service svc, String path, Object body) {
        return postNoAuth(svc, path, body, DEFAULT_STATUS_CODE_POST);
    }
}