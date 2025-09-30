import info.voriq.testing.config.ConfigManager;
import info.voriq.testing.utils.Service;
import info.voriq.testing.utils.TestDataHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import info.voriq.testing.CarResolve;
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
        apiWrapper.sendGetRequest(Service.CATALOG, resolve(suffixKeyOrLiteral))
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }

    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getCarId() {
        CarResolve carResolve = TestDataHelper.defaults(BaseApiTest::getConfig);

//        apiWrapper.sendPostRequest(Service.CATALOG, resolve("objectCarResolve"), carResolve)
//                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
//                .body("carId", equalTo(Integer.parseInt(resolve("carId"))));

        apiWrapper.sendPostRequest(Service.CATALOG, ConfigManager.objectCarCatalog(), carResolve)
                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
                .body("carId", equalTo(Integer.parseInt(resolve("carId"))));



    }

    int delay = 2000;

    @Test
    @Tag("positive")
    @Epic("API Tests")
    @Owner("Borys Pedorenko")
    public void testController() {
        apiWrapper.sendGetRequestWithDelayWithoutBodyStatusCode(
                Service.CATALOG,
                //resolve("objectTestController"),
                ConfigManager.objectTestDelay(),
                delay,
                204
        ).log().all();
    }
}