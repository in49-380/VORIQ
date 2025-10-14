
import info.voriq.testing.utils.Service;
import info.voriq.testing.utils.TestDataHelper;
import info.voriq.testing.config.ConfigManager;
import info.voriq.testing.CarResolve;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class NegativeCarCatalogTest extends BaseApiTest {

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("GET /v1/catalog/brands without authorization")
    public void getAllBrandsWithoutAuthorization() {

        apiWrapper.getNoAuth(Service.CATALOG, ConfigManager.objectCarBrands(), 401)
                .body(matchesJsonSchemaInClasspath("error_response-schema.json"))
                .body("error", equalTo("Unauthorized"))
                .body("message[0]", equalTo("Unauthorized access"));
    }

    final int DELAY = -1000;

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("Bad GET query with delay < 0 value")

    public void testController() {

        apiWrapper.get(Service.CATALOG, ConfigManager.objectTestDelay(), DELAY, 400)
                .body(matchesJsonSchemaInClasspath("error_bad_request-schema.json"))
                .body("validationErrors[0].field", nullValue())
                .body("message[0]", containsString("Delay should be more than"));
    }


    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> objectCarResolveMax() {
        return TestDataHelper.carResolveMAX(BaseApiTest::getConfig);
    }

    @ParameterizedTest(name = "[{index}] GET {0}")
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("Bad GET query with non-existent positive value")
    @MethodSource("objectCarResolveMax")
    void getCarIdCarBadRequestMax(CarResolve carResolve) {
        apiWrapper.post(Service.CATALOG, ConfigManager.objectCarResolve(), carResolve, 404)
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_response-schema.json"))
                .body("message[0]", equalTo("Car not found"));
    }


    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> objectCarResolveNull() {
        return TestDataHelper.carResolveNullArgs(BaseApiTest::getConfig);
    }


    @ParameterizedTest(name = "[{index}] GET {0}")
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("Bad GET query with null value")
    @MethodSource("objectCarResolveNull")
    void getCarIdCarBadRequest_null(CarResolve carResolve) {
        apiWrapper.post(Service.CATALOG, ConfigManager.objectCarResolve(), carResolve, 400)
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_bad_request-schema.json"))
                .body("message[0]", equalTo("The error of validation of the request"));
    }


    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> objectCarResolveMinusOne() {
        return TestDataHelper.carResolveMinusOneArgs(BaseApiTest::getConfig);
    }

    @ParameterizedTest(name = "[{index}] GET {0}")
    @Tag("negative")
    @Owner("Borys Pedorenko")
    @DisplayName("Bad GET query with -1 value")
    @MethodSource("objectCarResolveMinusOne")
    void getCarIdCarBadRequest_minusOne(CarResolve carResolve) {
        apiWrapper.post(Service.CATALOG, ConfigManager.objectCarResolve(), carResolve, 400)
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_bad_request-schema.json"))
                .body("message[0]", equalTo("The error of validation of the request"));
    }
}