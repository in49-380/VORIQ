//import info.voriq.testing.config.ConfigManager;
//import info.voriq.testing.utils.ApiWrapper;
//import info.voriq.testing.utils.Service;
//import io.qameta.allure.restassured.AllureRestAssured;
//import io.restassured.builder.RequestSpecBuilder;
//import io.restassured.filter.log.LogDetail;
//import io.restassured.http.ContentType;
//import io.restassured.specification.RequestSpecification;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.EnumMap;
//import java.util.Map;
//import java.util.Properties;
//
//import static io.restassured.RestAssured.reset;

//public class BaseApiTest {
//
//
//    protected static Properties cfg;
//    public static final Map<Service, RequestSpecification> SPECS = new EnumMap<>(Service.class);
//
//    @BeforeAll
//    static void bootstrap() {
//        cfg = new Properties();
//        try (FileInputStream fis = new FileInputStream("src/main/resources/config_homework.properties")) {
//            cfg.load(fis);
//        } catch (IOException e) {
//            throw new IllegalStateException("Не удалось загрузить config_homework.properties", e);
//        }
//        SPECS.put(Service.CATALOG, baseSpec(cfg.getProperty("baseURI.catalog")));
//        SPECS.put(Service.SECURITY, baseSpec(cfg.getProperty("baseURI.security")));
//    }
//
//    @AfterEach
//    void cleanup() {
//        reset();
//    }
//
//
//    private static RequestSpecification baseSpec(String baseUri) {
//        return new RequestSpecBuilder()
//                .setBaseUri(baseUri)
//                .addHeader("Authorization", "Bearer " + cfg.getProperty("token"))
//                .setContentType(ContentType.JSON)
//                .log(LogDetail.URI)
//                .log(LogDetail.HEADERS)
//                .log(LogDetail.BODY)
//                .addFilter(new AllureRestAssured())
//                .build();
//    }
//
//    static String resolve(String templateKey) {
//        String s = getConfig(templateKey);
//        s = s.replace("{brandId}", getConfig("brandId"));
//        s = s.replace("{modelId}", getConfig("modelId"));
//        s = s.replace("{yearId}", getConfig("yearId"));
//        s = s.replace("{engineId}", getConfig("engineId"));
//        s = s.replace("{transmissionId}", getConfig("transmissionId"));
//        s = s.replace("{objectCarCatalog}", getConfig("objectCarCatalog"));
//        s = s.replace("{objectTokenIssuance}", getConfig("objectTokenIssuance"));
//        s = s.replace("{userId}", getConfig("userId"));
//        s = s.replace("{key}", getConfig("key"));
//        s = s.replace("{token}", getConfig("token"));
//        s = s.replace("{carId}", getConfig("carId"));
//
//
//        return s;
//    }

// пакет оставьте как у вас в проекте
// import'ы адаптируйте при необходимости


import info.voriq.testing.config.ConfigManager;
import info.voriq.testing.utils.ApiWrapper;
import info.voriq.testing.utils.Service;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;


import java.util.EnumMap;
import java.util.Map;

import static io.restassured.RestAssured.reset;

public class BaseApiTest {

    /** Готовые спеки по сервисам */
    public static final Map<Service, RequestSpecification> SPECS = new EnumMap<>(Service.class);

    @BeforeAll
    static void bootstrap() {
        // Спеки строим ТОЛЬКО через ConfigManager (baseURI из props внутри него; секреты — из ENV)
        SPECS.put(Service.CATALOG, catalogSpec());
        SPECS.put(Service.SECURITY, securitySpec());
    }

    @AfterEach
    void cleanup() {
        reset();
    }

    /** Базовая спека для каталога (без Authorization) */
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

    /** Базовая спека для security (с Authorization из ENV) */
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

    /** Доступ к спекам по enum-сервису (как и раньше) */
    RequestSpecification spec(Service svc) {
        RequestSpecification s = SPECS.get(svc);
        if (s == null) throw new IllegalArgumentException("Неизвестный сервис: " + svc);
        return s;
    }

    // --------------------- РЕЗОЛВИНГ ШАБЛОНОВ ---------------------

    /**
     * Если у вас в properties хранятся шаблоны путей (например "objectCarResolve={objectCarCatalog}/cars/resolve"),
     * используйте этот метод: он возьмет строку-шаблон по ключу через ConfigManager.template(key)
     * и подставит все плейсхолдеры значениями из ConfigManager.
     */
    static String getConfig(String templateKey) {
        String template = ConfigManager.template(templateKey);
        return resolve(template);
    }

    /**
     * Если шаблон у вас формируется прямо в коде — можно сразу сюда.
     * Поддерживаем те же плейсхолдеры, что были раньше.
     */
    static String resolve(String template) {
        String s = template;

        // IDs/значения (не секреты)
        s = s.replace("{brandId}", String.valueOf(ConfigManager.brandId()));
        s = s.replace("{modelId}", String.valueOf(ConfigManager.modelId()));
        s = s.replace("{yearId}", String.valueOf(ConfigManager.yearId()));
        s = s.replace("{engineId}", String.valueOf(ConfigManager.engineId()));
        s = s.replace("{transmissionId}", String.valueOf(ConfigManager.transmissionId()));
        s = s.replace("{wheelDriveId}", String.valueOf(ConfigManager.wheelDriveId()));
        s = s.replace("{carId}", String.valueOf(ConfigManager.carId()));

        // Пути/объекты (из props через методы ConfigManager)
        s = s.replace("{objectCarCatalog}", ConfigManager.objectCarCatalog());
        s = s.replace("{objectCarBrands}", ConfigManager.objectCarBrands());
        s = s.replace("{objectCarModels}", ConfigManager.objectCarModels());
        s = s.replace("{objectCarYears}", ConfigManager.objectCarYears());
        s = s.replace("{objectCarEngines}", ConfigManager.objectCarEngines());
        s = s.replace("{objectCarTransmissions}", ConfigManager.objectCarTransmissions());
        s = s.replace("{objectCarWheelDrive}", ConfigManager.objectCarWheelDrive());
        s = s.replace("{objectCarResolve}", ConfigManager.objectCarResolve());
        s = s.replace("{objectTestController}", ConfigManager.objectTestDelay());

        // Если у вас есть другие object* ключи в props — добавьте под них геттеры в ConfigManager и строки замены здесь:
        // s = s.replace("{objectTokenIssuance}", ConfigManager.objectTokenIssuance());

        // Секреты — ТОЛЬКО из ENV
        s = s.replace("{userId}", ConfigManager.userId());
        s = s.replace("{key}", ConfigManager.key());
        s = s.replace("{token}", ConfigManager.token());

        return s;
    }

    /** Совместимость с вашим ApiWrapper(this::spec) */
    protected final ApiWrapper apiWrapper = new ApiWrapper(this::spec);
}