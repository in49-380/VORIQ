import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class CarCatalogPositiveTest extends BaseHomeWorkTest {


    @Test
    @Tag("positive")
    public void getAllBrands() {
        if (idBrand == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token"))
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("brands-schema.json"))
                    .extract().response();
            idBrand = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
        }
    }

    @Test
    @Tag("positive")
    public void getAllModelsByBrand() {
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token"))
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands")
                            + "/" + idBrand + getConfig("objectCarModels"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("models-schema.json"))
                    .extract().response();
            idModel = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
        }
    }

    @Test
    @Tag("positive")
    public void getAllYearsByBrandAndModel() {
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            getAllModelsByBrand();
        }
        if (idYear == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token"))
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands")
                            + "/" + idBrand + getConfig("objectCarModels")
                            + "/" + idModel + getConfig("objectCarYears"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("years-schema.json"))
                    .extract().response();
            idYear = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
        }
    }

    @Test
    @Tag("positive")
    public void getAllEnginesByBrandModelAndYear() {
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            getAllModelsByBrand();
        }
        if (idYear == null) {
            getAllYearsByBrandAndModel();
        }
        if (idEngine == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token"))
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") +
                            "/" + idBrand + getConfig("objectCarModels") +
                            "/" + idModel + getConfig("objectCarYears") +
                            "/" + idYear + getConfig("objectCarEngines"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("engines-schema.json"))
                    .extract().response();
            idEngine = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
        }
    }

    @Test
    @Tag("positive")
    public void getAllTransmissionsByBrandModelYearAndEngines() {
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            getAllModelsByBrand();
        }
        if (idYear == null) {
            getAllYearsByBrandAndModel();
        }
        if (idEngine == null) {
            getAllEnginesByBrandModelAndYear();
        }
        if (idTransmission == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token"))
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") +
                            "/" + idBrand + getConfig("objectCarModels") +
                            "/" + idModel + getConfig("objectCarYears") +
                            "/" + idYear + getConfig("objectCarEngines") +
                            "/" + idEngine + getConfig("objectCarTransmissions"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("transmissions-schema.json"))
                    .extract().response();
            idTransmission = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
        }
    }

    @Test
    @Tag("positive")
    public void getAllWheelDriveByBrandModelYearEnginesANDTransmissions() {
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            getAllModelsByBrand();
        }
        if (idYear == null) {
            getAllYearsByBrandAndModel();
        }
        if (idEngine == null) {
            getAllEnginesByBrandModelAndYear();
        }
        if (idTransmission == null) {
            getAllTransmissionsByBrandModelYearAndEngines();
        }
        Response resp = given()
                .header("Authorization", "Bearer " + getConfig("token"))
                .when().log().ifValidationFails()
                .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") +
                        "/" + idBrand + getConfig("objectCarModels") +
                        "/" + idModel + getConfig("objectCarYears") +
                        "/" + idYear + getConfig("objectCarEngines") +
                        "/" + idEngine + getConfig("objectCarTransmissions") +
                        "/" + idTransmission + getConfig("objectCarWheelDrive"))
                .then().statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("wheel_drive-schema.json"))
                .extract().response();
        idWheelDrive = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
    }

    @Test
    @Tag("positive")
    public void getCarId() {

        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            getAllModelsByBrand();
        }
        if (idYear == null) {
            getAllYearsByBrandAndModel();
        }
        if (idEngine == null) {
            getAllEnginesByBrandModelAndYear();
        }
        if (idTransmission == null) {
            getAllTransmissionsByBrandModelYearAndEngines();
        }
        if (idWheelDrive == null) {
            getAllWheelDriveByBrandModelYearEnginesANDTransmissions();
        }
        CarResolve carResolve = new CarResolve(idBrand, idModel, idYear, idEngine, idTransmission, idWheelDrive);

        System.out.println(carResolve);
        given()
                .contentType(ContentType.JSON).body(carResolve)
                .header("Authorization", "Bearer " + getConfig("token"))
                .when().log().ifValidationFails().log().all()
                .post(getConfig("objectCarCatalog") + getConfig("objectCarResolve"))
                .then().statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
                .extract().response();
    }
}