import Utils.Service;
import Utils.TestDataHelper;
import config.ConfigManager;
import io.qameta.allure.Owner;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class PositiveCarCatalogTest extends BaseApiTest {


    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> getSuffixAndSchema() {
        return TestDataHelper.suffixAndSchema();
    }

    @ParameterizedTest(name = "[{index}] GET {0} -> {1}")
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @MethodSource("getSuffixAndSchema")
    public void get_by_suffix(String suffixKeyOrLiteral, String schemaFile) {
        apiWrapper.sendGetRequest(Service.CATALOG, ConfigManager.get(suffixKeyOrLiteral))
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getCarId() {
        CarResolve carResolve = TestDataHelper.defaults();

        apiWrapper.sendPostRequest(Service.CATALOG, ConfigManager.objectCarResolve(), carResolve)
                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
                .body("carId", equalTo(Integer.parseInt(ConfigManager.get("carId"))));
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getTransmission() {

        apiWrapper.sendGetRequest(Service.CATALOG, ConfigManager.objectCarTransmissions())
                .body(matchesJsonSchemaInClasspath("transmissions-schema.json"));
        System.out.println(ConfigManager.token());
        ;
    }


    int delay = 2000;

    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void testController() {
        apiWrapper.sendGetRequestWithDelayWithoutBodyStatusCode(
                Service.CATALOG,
                ConfigManager.get("objectTestController"),
                delay,
                204
        );
    }
}