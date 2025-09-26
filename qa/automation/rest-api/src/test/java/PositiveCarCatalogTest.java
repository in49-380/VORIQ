import Utils.Service;
import io.restassured.http.ContentType;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class PositiveCarCatalogTest extends BaseApiTest {


    @ParameterizedTest(name = "[{index}] GET {0} -> {1}")
    @Tag("positive")
    @MethodSource("suffixAndSchema")
    void get_by_suffix(String suffixKeyOrLiteral, String schemaFile, int expectedStatus) {
        apiWrapper.sendGetRequest(Service.CATALOG, resolve(suffixKeyOrLiteral)).log().all()
                .statusCode(expectedStatus)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }

    @Test
    @Tag("positive")
    public void getAllBrands() {
        apiWrapper.sendGetRequest(Service.CATALOG, resolve("objectCarBrands")).log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("car_brands-schema.json"));
    }

    @Test
    @Tag("positive")
    public void getCarId() {

        CarResolve carResolve = new CarResolve(
                Integer.parseInt(getConfig("brandId")),
                Integer.parseInt(getConfig("modelId")),
                Integer.parseInt(getConfig("yearId")),
                Integer.parseInt(getConfig("engineId")),
                Integer.parseInt(getConfig("transmissionId")),
                Integer.parseInt(getConfig("wheelDriveId")));

        apiWrapper.sendPostRequest(Service.CATALOG, resolve("objectCarResolve"), carResolve).statusCode(200);
    }


//        post(Service.CATALOG, resolve("objectCarResolve"), carResolve).log().all()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"));
//

//        given()
//                .contentType(ContentType.JSON).body(carResolve)
//                .header("Authorization", "Bearer " + getConfig("token"))
//                .when().log().ifValidationFails().log().all()
//                .post(getConfig("objectCarCatalog") + getConfig("objectCarResolve"))
//                .then().statusCode(200)
//                .contentType(ContentType.JSON)
//                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"));
//    }
//
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