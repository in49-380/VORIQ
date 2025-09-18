import io.restassured.http.ContentType;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class NegativeCarCatalogTest extends BaseHomeWorkTest {

    @Test
    @Tag("negative")
    public void getAllBrandsWithoutAuthorization() {

        given()
                .when().log().ifValidationFails()
                .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands"))
                .then().statusCode(401)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("error_respons-schema.json"))
                .body("error", equalTo("Unauthorized"))
                .body("message[0]", equalTo("Unauthorized access"));
    }

    @Test
    @Tag("negative")
    public void getCarIdCarNotFound() {

        CarResolve carResolve = new CarResolve(random0toN(IDMAX), random0toN(IDMAX), random0toN(IDMAX), random0toN(IDMAX), random0toN(IDMAX), random0toN(IDMAX));

        System.out.println(carResolve);
        given()
                .contentType(ContentType.JSON).body(carResolve)
                .header("Authorization", "Bearer " + getConfig("token"))
                .when().log().ifValidationFails().log().all()
                .post(getConfig("objectCarCatalog") + getConfig("objectCarResolve"))
                .then().statusCode(404)
                .contentType(ContentType.JSON).log().all()
                .body(matchesJsonSchemaInClasspath("error_respons-schema.json"))
                .body("message[0]", equalTo("Car not found"));
    }

    @Test
    @Tag("negative")
    public void getCarIdCarBadRequest() {

        CarResolve carResolve = new CarResolve(random0toN(IDMAX), null, random0toN(IDMAX), random0toN(IDMAX), random0toN(IDMAX), random0toN(IDMAX));

        System.out.println(carResolve);
        given()
                .contentType(ContentType.JSON).body(carResolve)
                .header("Authorization", "Bearer " + getConfig("token"))
                .when().log().ifValidationFails().log().all()
                .post(getConfig("objectCarCatalog") + getConfig("objectCarResolve"))
                .then().statusCode(400)
                .contentType(ContentType.JSON).log().all()
                .body(matchesJsonSchemaInClasspath("error_bed_request-schema.json"))
                .body("validationErrors.find { it.field == 'modelId' }.message",
                        equalTo("Model Id can not be null"));
    }


}
