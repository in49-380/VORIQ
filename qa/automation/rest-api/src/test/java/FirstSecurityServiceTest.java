//import info.voriq.testing.utils.Service;
//import info.voriq.testing.config.ConfigManager;
//import io.qameta.allure.Owner;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static io.restassured.RestAssured.given;
//import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
//import static org.hamcrest.Matchers.notNullValue;
//
//public class FirstSecurityServiceTest extends BaseApiTest {
//
//    @Test
//    @Owner("Borys Pedorenko")
//    public void firstTest() {
//        String userId = ConfigManager.userId();
//        String key = ConfigManager.key();
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("userId", userId);
//        body.put("key", key);
//
//        apiWrapper.sendPostRequestStatusCode(Service.SECURITY, ConfigManager.objectTokenIssuance(), body, 200)
//                .body(matchesJsonSchemaInClasspath("token_kontroller-schema.json"))
//                .body("accessToken", notNullValue());
//    }
//
//    @Test
//    @Disabled
//    @Owner("Borys Pedorenko")
//    public void secondTest() {
//        given()
//                .when().log().all()
//                .options(ConfigManager.objectTokenIssuance())
//                .then().log().all()
//                .statusCode(200);
//    }
//}