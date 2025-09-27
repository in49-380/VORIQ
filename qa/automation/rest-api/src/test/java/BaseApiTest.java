import Utils.ApiWrapper;
import Utils.Service;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static io.restassured.RestAssured.reset;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class BaseApiTest {

    static final Integer IDMAX = 99999;


    protected static Properties cfg;
    public static final Map<Service, RequestSpecification> SPECS = new EnumMap<>(Service.class);

    @BeforeAll
    static void bootstrap() {
        cfg = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config_homework.properties")) {
            cfg.load(fis);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось загрузить config_homework.properties", e);
        }
        SPECS.put(Service.CATALOG, baseSpec(cfg.getProperty("baseURI.catalog")));
        SPECS.put(Service.SECURITY, baseSpec(cfg.getProperty("baseURI.security")));
    }

    @AfterEach
    void cleanup() {
        reset();
    }


    private static RequestSpecification baseSpec(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addHeader("Authorization", "Bearer " + cfg.getProperty("token"))
                .setContentType(ContentType.JSON)
                .log(LogDetail.URI)
                .log(LogDetail.HEADERS)
                .log(LogDetail.BODY)
                .addFilter(new AllureRestAssured())
                .build();
    }

    static String resolve(String templateKey) {
        String s = getConfig(templateKey);
        s = s.replace("{brandId}", getConfig("brandId"));
        s = s.replace("{modelId}", getConfig("modelId"));
        s = s.replace("{yearId}", getConfig("yearId"));
        s = s.replace("{engineId}", getConfig("engineId"));
        s = s.replace("{transmissionId}", getConfig("transmissionId"));
        s = s.replace("{objectCarCatalog}", getConfig("objectCarCatalog"));
        return s;
    }


    RequestSpecification spec(Service svc) {
        RequestSpecification s = SPECS.get(svc);
        if (s == null) throw new IllegalArgumentException("Неизвестный сервис: " + svc);
        return s;
    }

    protected static String getConfig(String key) {
        return cfg.getProperty(key);
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

    protected final ApiWrapper apiWrapper = new ApiWrapper(this::spec);
}