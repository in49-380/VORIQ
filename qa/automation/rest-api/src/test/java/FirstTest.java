import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class FirstTest extends BaseHomeWorkTest {

    @Test
    public void getAllBrands() {

        given()
                .header("Authorization", "Bearer " + getConfig("token")).log().all()
                .when().log().ifValidationFails()
                .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands"))
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("car-schema.json"))
                .log().ifValidationFails();
        //.log().all();
    }

    @Test
    public void getAllModelsByBrand() {


        given()
                .header("Authorization", "Bearer " + getConfig("token")).log().all()
                .when().log().ifValidationFails()
                .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands") + "/" + get11AllBrands().get(1) + getConfig("objectCarModels"))
                .then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("car-schema.json"))
                //.log().ifValidationFails();
                .log().all();


    }







    public List<Integer> get11AllBrands() {

        Response resp = (Response) given()
                .header("Authorization", "Bearer " + getConfig("token")).log().all()
                .when().log().ifValidationFails()
                .get(getConfig("objectCarCatalog") + getConfig("objectCarBrands"))
                .then().statusCode(200)
                .extract().response();
        return resp.jsonPath().getList("id");
        //.log().all();
    }

}