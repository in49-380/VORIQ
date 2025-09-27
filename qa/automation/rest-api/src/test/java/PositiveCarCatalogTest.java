import Utils.Service;
import io.qameta.allure.Owner;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;


public class PositiveCarCatalogTest extends BaseApiTest {


    @ParameterizedTest(name = "[{index}] GET {0} -> {1}")
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @MethodSource("suffixAndSchema")
    void get_by_suffix(String suffixKeyOrLiteral, String schemaFile) {
        apiWrapper.sendGetRequest(Service.CATALOG, resolve(suffixKeyOrLiteral))
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getCarId() {

        CarResolve carResolve = new CarResolve(
                Integer.parseInt(getConfig("brandId")),
                Integer.parseInt(getConfig("modelId")),
                Integer.parseInt(getConfig("yearId")),
                Integer.parseInt(getConfig("engineId")),
                Integer.parseInt(getConfig("transmissionId")),
                Integer.parseInt(getConfig("wheelDriveId")));

        apiWrapper.sendPostRequest(Service.CATALOG, resolve("objectCarResolve"), carResolve).log().all()
                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
                .body("carId", equalTo(1));
    }


//
//    @Test
//    @Tag("positive")
//    public void testController() {
//
//        given()
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + getConfig("token"))
//                .when().log().ifValidationFails()
//                .post(getConfig("objectCarCatalog") + getConfig("objectTestController"))
//                .then().statusCode(204)
//                .contentType(ContentType.JSON);
//    }
}