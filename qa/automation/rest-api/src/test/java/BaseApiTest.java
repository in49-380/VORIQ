import info.voriq.testing.utils.ApiWrapper;
import info.voriq.testing.utils.Service;
import info.voriq.testing.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;


import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.reset;

public class BaseApiTest {


    protected static Properties cfg;
    public static final Map<Service, RequestSpecification> SPECS = new EnumMap<>(Service.class);

    @BeforeAll
    static void bootstrap() {
        SPECS.put(Service.CATALOG, catalogSpec());
        SPECS.put(Service.SECURITY, securitySpec());
    }

    @AfterEach
    void cleanup() {
        reset();
    }


    private static RequestSpecification catalogSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.catalogBaseUri())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + ConfigManager.token())
                .log(LogDetail.URI)
                .log(LogDetail.HEADERS)
                .log(LogDetail.BODY)
                .addFilter(new AllureRestAssured())
                .build();
    }

    /**
     * Базовая спека для security (с Authorization из ENV)
     */
    private static RequestSpecification securitySpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.securityBaseUri())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + ConfigManager.securityToken())
                .log(LogDetail.URI)
                .log(LogDetail.HEADERS)
                .log(LogDetail.BODY)
                .addFilter(new AllureRestAssured())
                .build();
    }

    /**
     * Доступ к спекам по enum-сервису (как и раньше)
     */
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