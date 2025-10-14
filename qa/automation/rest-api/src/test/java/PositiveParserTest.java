import info.voriq.testing.config.ConfigManager;
import info.voriq.testing.utils.Service;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class PositiveParserTest extends BaseApiTest {

    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @DisplayName("Positive test - baseURI.parser receiving all cars")
    public void receivingAllCars() {
        apiWrapper.getNoAuth(Service.PARSER, ConfigManager.objectParserCar())
                .body(matchesJsonSchemaInClasspath("parser/cars_parser-schema.json"));
    }

    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @DisplayName("Positive test - baseURI.parser receiving a car by ID")
    public void receivingCarById() {
        apiWrapper.getNoAuth(Service.PARSER, ConfigManager.objectParserCarById())
                .body(matchesJsonSchemaInClasspath("parser/car_id_parser-schema.json")).log().all();
    }

    @Test
    @Tag("positive")
    @Owner("Borys Pedorenko")
    @DisplayName("Positive test - baseURI.parser receiving all engines")
    public void receivingAllEngines() {
        apiWrapper.getNoAuth(Service.PARSER, ConfigManager.objectParserEngines())
                .body(matchesJsonSchemaInClasspath("parser/engines_parser-schema.json"));
    }
}