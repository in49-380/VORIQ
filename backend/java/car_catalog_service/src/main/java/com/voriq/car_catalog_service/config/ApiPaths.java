package com.voriq.car_catalog_service.config;

/**
 * Centralized definition of REST API path constants used in the Car Catalog Service.
 * <p>
 * This class provides a single source of truth for all endpoint URIs,
 * ensuring consistency across controllers, tests, and API documentation.
 * <p>
 * The class is marked {@code final} and has a private constructor to prevent instantiation.
 * @since 1.0.0
 * @author RsLan
 */
public final class ApiPaths {

    /**
     * Base URL for catalog-related endpoints.
     */
    public static final String BASE_URL = "/v1/catalog";

    /**
     * Path for retrieving car brands.
     */
    public static final String BRANDS = "/brands";

    /**
     * Path for retrieving models by brand ID.
     */
    public static final String MODELS = "/brands/{brandId}/models";

    /**
     * Path for retrieving production years by brand and model ID.
     */
    public static final String YEARS = "/brands/{brandId}/models/{modelId}/years";

    /**
     * Path for retrieving engines by brand, model, and year ID.
     */
    public static final String ENGINES = "/brands/{brandId}/models/{modelId}/years/{yearId}/engines";

    /**
     * Path for retrieving transmissions by brand, model, year, and engine ID.
     */
    public static final String TRANSMISSIONS = "/brands/{brandId}/models/{modelId}/years/{yearId}/engines/{engineId}/transmissions";

    /**
     * Path for retrieving wheel drives by brand, model, year, engine, and transmission ID.
     */
    public static final String WHEEL_DRIVES = "/brands/{brandId}/models/{modelId}/years/{yearId}/" +
            "engines/{engineId}/transmissions/{transmissionsId}/wheel_drive";

    /**
     * Path for resolving a car configuration into a specific car ID.
     */
    public static final String RESOLVE = "/cars/resolve";

    // --- Combined URLs with base path ---

    /**
     * Full URL for brands endpoint.
     */
    public static final String BRANDS_URL = BASE_URL + BRANDS;

    /**
     * Full URL for models endpoint.
     */
    public static final String MODELS_URL = BASE_URL + MODELS;

    /**
     * Full URL for years endpoint.
     */
    public static final String YEARS_URL = BASE_URL + YEARS;

    /**
     * Full URL for engines endpoint.
     */
    public static final String ENGINES_URL = BASE_URL + ENGINES;

    /**
     * Full URL for transmissions endpoint.
     */
    public static final String TRANSMISSIONS_URL = BASE_URL + TRANSMISSIONS;

    /**
     * Full URL for wheel drives endpoint.
     */
    public static final String WHEEL_DRIVES_URL = BASE_URL + WHEEL_DRIVES;

    /**
     * Full URL for car resolution endpoint.
     */
    public static final String RESOLVE_URL = BASE_URL + RESOLVE;

    // --- Test-related endpoints ---

    /**
     * Base URL for test endpoints.
     */
    public static final String TEST_BASE_URL = "/v1/test";

    /**
     * Path for simulating delayed responses (in milliseconds).
     */
    public static final String DELAY = "/delay-ms";

    /**
     * Full URL for test delay endpoint.
     */
    public static final String TEST_DELAY_URL = TEST_BASE_URL + DELAY;

    /**
     * Private constructor to prevent instantiation.
     */
    private ApiPaths() {
    }
}
