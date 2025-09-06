import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class FirstSecurityServiceTest extends BaseSecurityTest {

    @Test
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
}
