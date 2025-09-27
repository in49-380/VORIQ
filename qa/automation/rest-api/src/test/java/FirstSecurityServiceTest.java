import io.qameta.allure.Owner;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class FirstSecurityServiceTest extends BaseApiTest {

    @Test
    @Owner("Borys Pedorenko")
    public void firstTest() {
        String userId = getConfig("userId");
        String key = getConfig("key");

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("key", key);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                //.log().all()
                .when()
                .post(getConfig("objectTokenIssuance"))
                .then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON);
        //.log().all();
    }

    @Test
    @Disabled
    @Owner("Borys Pedorenko")
    public void secondTest() {
        given()
                .when().log().all()
                .options(getConfig("objectTokenIssuance"))
                .then().log().all()
                .statusCode(200);
    }
}