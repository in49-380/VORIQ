package config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigManager {
    private static final Properties PROPS = new Properties();
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z0-9_.-]+)\\}");
    private static final int MAX_DEPTH = 12;

    private static final String ENV_TOKEN = "VORIQ_TOKEN";
    private static final String ENV_TOKEN_SECURITY = "VORIQ_TOKEN_SECURITY";
    private static final String ENV_USER_ID = "VORIQ_USER_ID";
    private static final String ENV_KEY = "VORIQ_KEY";

    static {
        try (InputStream is = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config_homework.properties")) {
            if (is == null) throw new IllegalStateException("config_homework.properties not found in classpath");
            PROPS.load(new java.io.InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
        // Опционально: перекрыть системными и ENV
        System.getProperties().forEach((k, v) -> PROPS.put(k, v));
        // Если хочешь, чтобы ENV имели приоритет над файлом — используй put:
        System.getenv().forEach((k, v) -> PROPS.putIfAbsent(k, v));
    }

    private ConfigManager() {}

    public static String catalogBaseUri()       { return get("baseURI.catalog"); }
    public static String securityBaseUri()      { return get("baseURI.security"); }
    public static String objectCarCatalog()     { return get("objectCarCatalog"); }
    public static String objectCarBrands()      { return get("objectCarBrands"); }
    public static String objectCarModels()      { return get("objectCarModels"); }
    public static String objectCarYears()       { return get("objectCarYears"); }
    public static String objectCarEngines()     { return get("objectCarEngines"); }
    public static String objectCarTransmissions(){ return get("objectCarTransmissions"); }
    public static String objectCarWheelDrive()  { return get("objectCarWheelDrive"); }
    public static String objectCarResolve()     { return get("objectCarResolve"); }
    public static String objectTestDelay()      { return get("objectTestController"); }

    // -------- тестовые ID (не секреты, можно оставить дефолты) --------
    public static Integer brandId()        { return Integer.parseInt(get("brandId")); }
    public static Integer modelId()        { return Integer.parseInt(get("modelId")); }
    public static Integer yearId()         { return Integer.parseInt(get("yearId")); }
    public static Integer engineId()       { return Integer.parseInt(get("engineId")); }
    public static Integer transmissionId() { return Integer.parseInt(get("transmissionId")); }
    public static Integer wheelDriveId()   { return Integer.parseInt(get("wheelDriveId")); }
    public static Integer carId()          { return Integer.parseInt(get("carId")); }

    // -------- секреты (ТОЛЬКО из ENV/Secrets) --------
    public static String token()          { return env(ENV_TOKEN, "API token"); }
    public static String securityToken()  { return env(ENV_TOKEN_SECURITY, "Security service token"); }
    public static String userId()         { return env(ENV_USER_ID, "User ID"); }
    public static String key()            { return env(ENV_KEY, "API key"); }


    private static String env(String name, String human) {
        String v = System.getenv(name);
        System.out.println("ENV: " + name + " = " + v);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(human + " is not set in ENV: " + name);
        }
        return v;
    }





    /** Получить значение ключа с полной подстановкой (сначала overrides, потом config). */
    public static String get(String key, Map<String, ?> overrides) {
        String raw = PROPS.getProperty(key);
        if (raw == null) {
            throw new IllegalStateException("Config key not found: " + key);
        }

        return resolveAll(raw, overrides);
    }

    /** Удобный varargs: get("objectCarEngines", "brandId",21,"modelId",137,...) */
    public static String get(String key, Object... kv) {
        return get(key, toMap(kv));
    }

    /** Без overrides — всё берём из config.properties. */
    public static String get(String key) {
        return get(key, Collections.emptyMap());
    }



    /** Собрать полный URL: base + path (оба с подстановкой placeholders). */
    public static String url(String baseKey, String pathKey, Object... kv) {
        Map<String, ?> overrides = toMap(kv);
        String base = get(baseKey, overrides);
        String path = get(pathKey, overrides);
        // Нормализуем слеши
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        } else if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    /** Рекурсивное раскрытие: приоритет overrides -> PROPS; повторяем до стабилизации. */
    private static String resolveAll(String input, Map<String, ?> overrides) {
        String result = input;
        for (int depth = 0; depth < MAX_DEPTH; depth++) {
            Matcher m = PLACEHOLDER.matcher(result);
            boolean changed = false;
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String name = m.group(1);
                String replacement = null;

                // 1) приоритет override из теста
                if (overrides.containsKey(name) && overrides.get(name) != null) {
                    replacement = String.valueOf(overrides.get(name));
                }
                // 2) иначе — из config.properties (ключ может быть как «листом», так и «шаблоном»)
                else if (PROPS.containsKey(name)) {
                    replacement = PROPS.getProperty(name);
                }

                if (replacement != null) {
                    m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                    changed = true;
                } else {
                    // Оставляем плейсхолдер как есть — возможно раскроется на следующей итерации
                    m.appendReplacement(sb, Matcher.quoteReplacement("{" + name + "}"));
                }
            }
            m.appendTail(sb);
            result = sb.toString();
            if (!changed) break;
        }

        // После итераций никаких «неизвестных» плейсхолдеров остаться не должно
        Set<String> missing = findUnresolved(result, overrides.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Unresolved placeholders: " + String.join(", ", missing));
        }
        return result;
    }

    private static Set<String> findUnresolved(String s, Set<String> overrideKeys) {
        Matcher m = PLACEHOLDER.matcher(s);
        Set<String> missing = new LinkedHashSet<>();
        while (m.find()) {
            String name = m.group(1);
            if (!overrideKeys.contains(name) && !PROPS.containsKey(name)) {
                missing.add(name);
            }
        }
        return missing;
    }

    private static Map<String, Object> toMap(Object... kv) {
        if (kv == null || kv.length == 0) return Collections.emptyMap();
        if (kv.length % 2 != 0) throw new IllegalArgumentException("Key-Value pairs expected");
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
