import Utils.Service;
import io.qameta.allure.Owner;
import io.restassured.http.ContentType;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class NegativeCarCatalogTest extends BaseApiTest {

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")
    public void getAllBrandsWithoutAuthorization() {


        given()
                .when().log().ifValidationFails().log().all()
                .get(getConfig("baseURI.catalog") + resolve("objectCarCatalog") + resolve("objectCarBrands"))
                .then().statusCode(401)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("error_respons-schema.json"))
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
                .body(matchesJsonSchemaInClasspath("error_respons-schema.json"))
                .body("message[0]", equalTo("Car not found"));
    }

    @Test
    @Tag("negative")
    @Owner("Borys Pedorenko")
    public void getCarIdCarBadRequest() {

        CarResolve carResolve = new CarResolve(
                Integer.parseInt(getConfig("brandId")),
                Integer.parseInt(getConfig("modelId")),
                null,
                Integer.parseInt(getConfig("engineId")),
                Integer.parseInt(getConfig("transmissionId")),
                Integer.parseInt(getConfig("wheelDriveId")));

        apiWrapper.sendPostRequestStatusCode(Service.CATALOG, resolve("objectCarResolve"), carResolve, 400)
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("error_bed_request-schema.json"))
                .log().all();
    }
}