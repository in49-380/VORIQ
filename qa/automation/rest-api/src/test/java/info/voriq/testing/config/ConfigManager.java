package info.voriq.testing.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class ConfigManager {
    private static final String DEFAULT_PROPS = "config_homework.properties";
    private static final Properties P = new Properties();

    private static final String ENV_TOKEN = "VORIQ_TOKEN";
    private static final String ENV_TOKEN_SECURITY = "VORIQ_TOKEN_SECURITY";
    private static final String ENV_USER_ID = "VORIQ_USER_ID";
    private static final String ENV_KEY = "VORIQ_KEY";

    private ConfigManager() {}

    static {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(DEFAULT_PROPS)) {
            if (is != null) P.load(is);
            else throw new IllegalStateException("Cannot load " + DEFAULT_PROPS);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    // -------- base URIs / endpoints (из .properties, НЕ секреты) --------
    public static String catalogBaseUri()       { return req("baseURI.catalog"); }
    public static String securityBaseUri()      { return req("baseURI.security"); }
    public static String objectCarCatalog()     { return req("objectCarCatalog"); }
    public static String objectCarBrands()      { return req("objectCarBrands"); }
    public static String objectCarModels()      { return req("objectCarModels"); }
    public static String objectCarYears()       { return req("objectCarYears"); }
    public static String objectCarEngines()     { return req("objectCarEngines"); }
    public static String objectCarTransmissions(){ return req("objectCarTransmissions"); }
    public static String objectCarWheelDrive()  { return req("objectCarWheelDrive"); }
    public static String objectCarResolve()     { return req("objectCarResolve"); }
    public static String objectTestDelay()      { return req("objectTestController"); }

    // -------- тестовые ID (не секреты, можно оставить дефолты) --------
    public static Integer brandId()        { return Integer.parseInt(opt("brandId", "20")); }
    public static Integer modelId()        { return Integer.parseInt(opt("modelId", "137")); }
    public static Integer yearId()         { return Integer.parseInt(opt("yearId", "100")); }
    public static Integer engineId()       { return Integer.parseInt(opt("engineId", "282")); }
    public static Integer transmissionId() { return Integer.parseInt(opt("transmissionId", "104")); }
    public static Integer wheelDriveId()   { return Integer.parseInt(opt("wheelDriveId", "33")); }
    public static Integer carId()          { return Integer.parseInt(opt("carId", "381")); }

    // -------- секреты (ТОЛЬКО из ENV/Secrets) --------
    public static String token()          { return env(ENV_TOKEN, "API token"); }
    public static String securityToken()  { return env(ENV_TOKEN_SECURITY, "Security service token"); }
    public static String userId()         { return env(ENV_USER_ID, "User ID"); }
    public static String key()            { return env(ENV_KEY, "API key"); }

    // -------- helpers --------
    private static String req(String key) {
        String v = P.getProperty(key);
        return Objects.requireNonNull(v, "Missing property: " + key);
    }
    private static String opt(String key, String def) { return P.getProperty(key, def); }
    private static String env(String name, String human) {
        String v = System.getenv(name);
        System.out.println("ENV: " + name + " = " + v);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(human + " is not set in ENV: " + name);
        }
        return v;
    }
    /** Возвращает сырое значение по ключу из config_homework.properties (для шаблонов путей и т.п.). */
    public static String template(String key) {
        // используем уже существующий приватный req(key)
        return req(key);
    }


}
