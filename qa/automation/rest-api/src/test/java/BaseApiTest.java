import io.qameta.allure.Step;
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
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.reset;

public  class BaseApiTest {

    static Integer idCar;
    static Integer idBrand;
    static Integer idModel;
    static Integer idYear;
    static Integer idEngine;
    static Integer idTransmission;
    static Integer idWheelDrive;
    static final Integer IDMAX = 99999;


    /** Перечень ваших “бэкендов”/доменов. Дополните при необходимости. */
    protected enum Service { CATALOG, SECURITY }

    protected static Properties cfg;
    private static final Map<Service, RequestSpecification> SPECS = new EnumMap<>(Service.class);

    @BeforeAll
    static void bootstrap() {
        cfg = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config_homework.properties")) {
            cfg.load(fis);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось загрузить config_homework.properties", e);
        }

        // Готовим спецификации один раз
        SPECS.put(Service.CATALOG, baseSpec(cfg.getProperty("baseURI.catalog")));
        SPECS.put(Service.SECURITY, baseSpec(cfg.getProperty("baseURI.security")));
    }

    @AfterEach
    void cleanup() {
        reset(); // на всякий случай сброс глобальных настроек RestAssured
    }

    /** Общая спецификация: логирование, Allure, типы, заголовки и т.д. */
    private static RequestSpecification baseSpec(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addHeader("Authorization", "Bearer " + cfg.getProperty("token"))
                .setContentType(ContentType.JSON)
                .log(LogDetail.URI)
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected static String resolve(String templateKey) {
        String s = getConfig(templateKey);
        // подставляем все известные ID из конфигурации
        s = s.replace("{brandId}", getConfig("brandId"));
        s = s.replace("{modelId}", getConfig("modelId"));
        s = s.replace("{yearId}", getConfig("yearId"));
        s = s.replace("{engineId}", getConfig("engineId"));
        s = s.replace("{transmissionId}", getConfig("transmissionId"));
        s = s.replace("{objectCarCatalog}", getConfig("objectCarCatalog"));

        return s;
    }


    /** Достаём нужную спецификацию. */
    protected RequestSpecification spec(Service svc) {
        RequestSpecification s = SPECS.get(svc);
        if (s == null) throw new IllegalArgumentException("Неизвестный сервис: " + svc);
        return s;
    }

    /** Утилиты на всех. */
    protected static String getConfig(String key) { return cfg.getProperty(key); }

    protected static int random0toN(int n) {
        if (n < 0) throw new IllegalArgumentException("n >= 0");
        return ThreadLocalRandom.current().nextInt(n + 1);
    }

     //(Опционально) компактные шаги-обёртки, если любите выносить вызовы:
    protected final Api api = new Api();

    protected class Api {
        @Step("GET {path} [{svc}]")
        public io.restassured.response.ValidatableResponse get(Service svc, String path) {
            return io.restassured.RestAssured
                    .given().spec(spec(svc))
                    .when().get(path)
                    .then();
        }

        @Step("POST {path} [{svc}]")
        public io.restassured.response.ValidatableResponse post(Service svc, String path, Object body) {
            return io.restassured.RestAssured
                    .given().spec(spec(svc)).body(body)
                    .when().post(path)
                    .then();
        }

        @Step("DELETE {path} [{svc}]")
        public io.restassured.response.ValidatableResponse delete(Service svc, String path) {
            return io.restassured.RestAssured
                    .given().spec(spec(svc))
                    .when().delete(path)
                    .then();
        }

        // добавляйте put/patch по необходимости
    }}

