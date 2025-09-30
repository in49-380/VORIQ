import Utils.ApiWrapper;
import Utils.Service;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.reset;

public class BaseApiTest {


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
        s = s.replace("{objectTokenIssuance}", getConfig("objectTokenIssuance"));
        s = s.replace("{userId}", getConfig("userId"));
        s = s.replace("{key}", getConfig("key"));
        s = s.replace("{token}", getConfig("token"));
        s = s.replace("{carId}", getConfig("carId"));


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

    protected final ApiWrapper apiWrapper = new ApiWrapper(this::spec);
}