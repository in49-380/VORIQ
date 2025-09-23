import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.CarResolve;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PositiveCarCatalogTest extends BaseApiTest {


    @ParameterizedTest(name = "[{index}] GET {0} -> {1}")
    @Tag("positive")
    @MethodSource("suffixAndSchema")
        // меняем ТОЛЬКО вторую часть пути и схему
    void get_by_suffix(String suffixKeyOrLiteral, String schemaFile, int expectedStatus) {

       // String path = resolve(suffixKeyOrLiteral);

        api.get(Service.CATALOG, resolve(suffixKeyOrLiteral))
                .statusCode(expectedStatus)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath(schemaFile));
    }

    static Stream<Arguments> suffixAndSchema() {
        return Stream.of(
                // через ключи из config.properties
                arguments("objectCarBrands", "brands-schema.json", 200),
                arguments("objectCarModels", "models-schema.json", 200),
                arguments("objectCarYears", "years-schema.json", 200),
                arguments("objectCarEngines", "engines-schema.json", 200),
                arguments("objectCarTransmissions", "transmissions-schema.json", 200),
                arguments("objectCarWheelDrive", "wheel_drive-schema.json", 200)
        );
    }


    @Test
    @Tag("positive")
    public void testController() {

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getConfig("token"))
                .when().log().ifValidationFails()
                .post(getConfig("objectCarCatalog") + getConfig("objectTestController"))
                .then().statusCode(204)
                .contentType(ContentType.JSON);
    }
}