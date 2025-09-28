import Utils.Service;
import io.qameta.allure.Owner;
import io.restassured.http.ContentType;
import org.example.CarResolve;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class NegativeCarCatalogTest extends BaseApiTest {

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("GET /v1/catalog/brands without authorization")
    public void getAllBrandsWithoutAuthorization() {

        given()
                .when().log().ifValidationFails()
                .get(getConfig("baseURI.catalog") + resolve("objectCarCatalog") + resolve("objectCarBrands"))
                .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("error_response-schema.json"))
                .body("error", equalTo("Unauthorized"))
                .body("message[0]", equalTo("Unauthorized access"));
    }

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")
    public void getCarIdCarNotFound() {

        CarResolve carResolve = new CarResolve(
                Integer.parseInt(getConfig("brandId")),
                Integer.parseInt(getConfig("modelId")),
                Integer.parseInt(getConfig("yearId")),
                IDMAX,
                Integer.parseInt(getConfig("transmissionId")),
                Integer.parseInt(getConfig("wheelDriveId")));

        apiWrapper.sendPostRequestStatusCode(Service.CATALOG, resolve("objectCarResolve"), carResolve, 404).log().all()
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_response-schema.json"))
                .body("message[0]", equalTo("Car not found"));
    }

    @ParameterizedTest(name = "[{index}] GET {0}")
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("Request for a car ID if the value of one of the parameters is null")
    @MethodSource("ObjectCarResolveNull")
    public void getCarIdCarBadRequest(CarResolve carResolve) {

        apiWrapper.sendPostRequestStatusCode(Service.CATALOG, resolve("objectCarResolve"), carResolve, 400)
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_bed_request-schema.json"))
                .body("message[0]", equalTo("The error of validation of the request"));
    }


    static Stream<Arguments> ObjectCarResolveNull() {
        return Stream.of(
                arguments(new CarResolve(null,
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        null,
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        null,
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        null,
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        null,
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        null)));
    }

    @ParameterizedTest(name = "[{index}] GET {0}")
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("Request for a car ID if the value of one of the parameters is less than 0")
    @MethodSource("ObjectCarResolveMinus")
    public void getCarIdCarBadRequestMinus(CarResolve carResolve) {

        apiWrapper.sendPostRequestStatusCode(Service.CATALOG, resolve("objectCarResolve"), carResolve, 400)
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_bed_request-schema.json"))
                .body("message[0]", equalTo("The error of validation of the request"));
    }

    static Stream<Arguments> ObjectCarResolveMinus() {

        return Stream.of(
                arguments(new CarResolve(-1,
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        -1,
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        -1,
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        -1,
                        Integer.parseInt(getConfig("transmissionId")),
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        -1,
                        Integer.parseInt(getConfig("wheelDriveId")))),
                arguments(new CarResolve(Integer.parseInt(getConfig("brandId")),
                        Integer.parseInt(getConfig("modelId")),
                        Integer.parseInt(getConfig("yearId")),
                        Integer.parseInt(getConfig("engineId")),
                        Integer.parseInt(getConfig("transmissionId")),
                        -1)));
    }







    int delay = -1000;

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")

    public void testController() {

        apiWrapper.sendGetRequestWithDelayWithoutBodyStatusCode(Service.CATALOG,
                        resolve("objectTestController"), delay, 400).log().all()
                .body(matchesJsonSchemaInClasspath("error_bed_request-schema.json"))
                .body("validationErrors[0].field", nullValue())
                .body("validationErrors.message", hasItem(containsString("Delay should be more than ")));
    }
}