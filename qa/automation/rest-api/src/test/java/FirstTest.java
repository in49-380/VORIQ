import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class FirstTest extends BaseHomeWorkTest {

    @Test
    @Disabled("Временно выключен")
    public void firstTest() {

        given()
                .header("Authorization", "Bearer " + getConfig("token")).log().all()
                .when()
                .get(getConfig("objectYear"))
                .then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schema.json"))
                //.log().ifValidationFails();
                .log().all();
    }
}