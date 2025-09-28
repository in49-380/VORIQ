import Utils.Service;
import io.qameta.allure.Owner;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class PositiveCarCatalogTest extends BaseApiTest {


    @ParameterizedTest(name = "[{index}] GET {0} -> {1}")
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @MethodSource("suffixAndSchema")
    public void get_by_suffix(String suffixKeyOrLiteral, String schemaFile) {
        apiWrapper.sendGetRequest(Service.CATALOG, resolve(suffixKeyOrLiteral))
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }

    static Stream<Arguments> suffixAndSchema() {
        return Stream.of(
                arguments("objectCarBrands", "brands-schema.json"),
                arguments("objectCarModels", "models-schema.json"),
                arguments("objectCarYears", "years-schema.json"),
                arguments("objectCarEngines", "engines-schema.json"),
                arguments("objectCarTransmissions", "transmissions-schema.json"),
                arguments("objectCarWheelDrive", "wheel_drive-schema.json")
        );
    }


    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    public void getCarId() {

        CarResolve carResolve = new CarResolve(
                Integer.parseInt(getConfig("brandId")),
                Integer.parseInt(getConfig("modelId")),
                Integer.parseInt(getConfig("yearId")),
                Integer.parseInt(getConfig("engineId")),
                Integer.parseInt(getConfig("transmissionId")),
                Integer.parseInt(getConfig("wheelDriveId")));

        apiWrapper.sendPostRequest(Service.CATALOG, resolve("objectCarResolve"), carResolve).log().all()
                .body(matchesJsonSchemaInClasspath("car_catalog-schema.json"))
                .body("carId", equalTo(1));
    }


    int delay = 2000;

    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")

    public void testController() {

        apiWrapper.sendGetRequestWithDelayWithoutBodyStatusCode(Service.CATALOG,
                resolve("objectTestController"),delay,204);
    }
}