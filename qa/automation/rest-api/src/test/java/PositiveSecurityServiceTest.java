import info.voriq.testing.utils.Service;
import info.voriq.testing.config.ConfigManager;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class PositiveSecurityServiceTest extends BaseApiTest {

    @Test
    @Owner("Borys Pedorenko")
    @Tag("negative")
    @DisplayName ("Positive test baseURI.security of token issuance")
    public void firstTest() {
        String userId = ConfigManager.userId();
        String key = ConfigManager.key();

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("key", key);

        apiWrapper.post(Service.SECURITY, ConfigManager.objectTokenIssuance(), body)
                .body(matchesJsonSchemaInClasspath("token_kontroller-schema.json"))
                .body("accessToken", notNullValue());
    }
}