import info.voriq.testing.utils.Service;
import info.voriq.testing.utils.TestDataHelper;
import info.voriq.testing.config.ConfigManager;
import io.qameta.allure.Owner;
import info.voriq.testing.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class PositiveCarCatalogTest extends BaseApiTest {
    static final int DELAY = 2000;

    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> getSuffixAndSchema() {
        return TestDataHelper.suffixAndSchema();
    }

    @ParameterizedTest(name = "[{index}] GET {0} -> {1}")
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @MethodSource("getSuffixAndSchema")
    public void get_by_suffix(String suffixKeyOrLiteral, String schemaFile) {
        apiWrapper.get(Service.CATALOG, ConfigManager.get(suffixKeyOrLiteral))
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getCarId() {
        CarResolve carResolve = TestDataHelper.defaults();

        apiWrapper.post(Service.CATALOG, ConfigManager.objectCarResolve(), carResolve)
                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
                .body("carId", equalTo(ConfigManager.carId()));
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getTransmission() {

        apiWrapper.get(Service.CATALOG, ConfigManager.objectCarTransmissions())
                .body(matchesJsonSchemaInClasspath("transmissions-schema.json"));
        System.out.println(ConfigManager.token());
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void testController() {
        apiWrapper.get(Service.CATALOG, ConfigManager.objectTestDelay(), DELAY, 204)
                .body(emptyOrNullString());
    }
}