import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class FirstTest extends BaseHomeWorkTest {


    @Test
    public void getAllBrands() {
        if (idBrand == null) {

            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token")).log().all()
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON).log().all()
                    .body(matchesJsonSchemaInClasspath("car-schema.json"))
                    .extract().response();
            idBrand = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
        }
        System.out.println("idBrand = " + idBrand);
    }

    @Test
    public void getAllModelsByBrand() {
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token")).log().all()
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") + "/" + idBrand + getConfig("objectCarModels"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON).log().all()
                    .body(matchesJsonSchemaInClasspath("car-schema.json"))
                    .extract().response();
            idModel = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
            System.out.println("idModel = " + idModel);
        }
    }


    @Test
    public void getAllYearsByBrandAndModel() {
        System.out.println("idBrand = " + idBrand);
        System.out.println("idModel = " + idModel);
        System.out.println("idYear = " + idYear);
        if (idBrand == null) {
            getAllBrands();
        }
        if (idModel == null) {
            getAllModelsByBrand();
        }
        if (idYear == null) {
            Response resp = given()
                    .header("Authorization", "Bearer " + getConfig("token")).log().all()
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") + "/" + idBrand + getConfig("objectCarModels") + "/" + idModel + getConfig("objectCarYears"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON).log().all()
                    .body(matchesJsonSchemaInClasspath("car-schema.json"))
                    .extract().response();
            idYear = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
            System.out.println("idYear = " + idYear);
        }
    }


    @Test
    public void getAllEnginesByBrandAndModelAndYear() {
        System.out.println("idBrand = " + idBrand);
        System.out.println("idModel = " + idModel);
        System.out.println("idYear = " + idYear);
        System.out.println("idEngine = " + idEngine);
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
                    .header("Authorization", "Bearer " + getConfig("token")).log().all()
                    .when().log().ifValidationFails()
                    .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") +
                            "/" + idBrand + getConfig("objectCarModels") +
                            "/" + idModel + getConfig("objectCarYears")+
                            "/" + idYear + getConfig("objectCarEngines"))
                    .then().statusCode(200)
                    .contentType(ContentType.JSON).log().all()
                    .body(matchesJsonSchemaInClasspath("car-schema.json"))
                    .extract().response();
            idEngine = (Integer) resp.jsonPath().getList("id").get(random0toN(resp.jsonPath().getList("id").size() - 1));
            System.out.println("idEngine = " + idEngine);
        }
    }

}







