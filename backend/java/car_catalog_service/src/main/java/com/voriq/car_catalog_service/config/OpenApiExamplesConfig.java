package com.voriq.car_catalog_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.voriq.car_catalog_service.config.ApiPaths.*;

@Configuration
public class OpenApiExamplesConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";
    private static final String TIMESTAMP_EXAMPLE = "2025-07-21T11:20:00Z"; // ISO-8601

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components()
                .addSecuritySchemes(BEARER_SCHEME_NAME, bearerScheme())

                // 400
                .addExamples("Error400MissingIdModels", ex400(MODELS_URL))

                .addExamples("Error400MissingIdYears", ex400(YEARS_URL))

                .addExamples("Error400MissingIdEngines", ex400(ENGINES_URL))

                .addExamples("Error400MissingIdTransmissions", ex400(TRANSMISSIONS_URL))

                .addExamples("Error400MissingIdWheelDrives", ex400(WHEEL_DRIVES_URL))

                .addExamples("Error400DtoHaveWrongValueResolve", ex(
                        "ModelId is wrong.",
                        "Model ID is either null or not in UUID format.",
                        errorExample(HttpStatus.BAD_REQUEST, RESOLVE_URL,
                                "Model id cannot be null", "modelId", "Model id cannot be null")
                ))

                // 401
                .addExamples("Error401UserUnauthorizedBrands", ex401(BRANDS_URL))

                .addExamples("Error401UserUnauthorizedModels", ex401(MODELS_URL))

                .addExamples("Error401UserUnauthorizedYears", ex401(YEARS_URL))

                .addExamples("Error401UserUnauthorizedEngines", ex401(ENGINES_URL))

                .addExamples("Error401UserUnauthorizedTransmissions", ex401(TRANSMISSIONS_URL))

                .addExamples("Error401UserUnauthorizedWheelDrives", ex401(WHEEL_DRIVES_URL))

                .addExamples("Error401UserUnauthorizedWheelResolve", ex401(RESOLVE_URL))

                // 404
                .addExamples("Error404CarNotFoundResolve", ex(
                        "Car not found.",
                        "Car not found.",
                        errorExample(HttpStatus.NOT_FOUND, RESOLVE_URL,
                                "Car not found.")
                ))

                // 500
                .addExamples("Error500TemporaryServiceErrorBrands", ex500(BRANDS_URL))

                .addExamples("Error500TemporaryServiceErrorModels", ex500(MODELS_URL))

                .addExamples("Error500TemporaryServiceErrorYears", ex500(YEARS_URL))

                .addExamples("Error500TemporaryServiceErrorEngines", ex500(ENGINES_URL))

                .addExamples("Error500TemporaryServiceErrorTransmissions", ex500(TRANSMISSIONS_URL))

                .addExamples("Error500TemporaryServiceErrorWheelDrives", ex500(WHEEL_DRIVES_URL))

                .addExamples("Error500TemporaryServiceErrorResolve", ex500(RESOLVE_URL))

                // 503
                .addExamples("Error503ServiceUnavailableBrands", ex503(BRANDS_URL))

                .addExamples("Error503ServiceUnavailableModels", ex503(MODELS_URL))

                .addExamples("Error503ServiceUnavailableYears", ex503(YEARS_URL))

                .addExamples("Error503ServiceUnavailableEngines", ex503(ENGINES_URL))

                .addExamples("Error503ServiceUnavailableTransmissions", ex503(TRANSMISSIONS_URL))

                .addExamples("Error503ServiceUnavailableWheelDrives", ex503(WHEEL_DRIVES_URL))

                .addExamples("Error503ServiceUnavailableResolve", ex503(RESOLVE_URL));

        return new OpenAPI()
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME));
    }

    private static Example ex(String summary, String description, Map<String, Object> value) {
        return new Example().summary(summary).description(description).value(value);
    }

    private static Example ex400(String url) {
        return ex(
                "ID is wrong.",
                "Item ID is incorrect, negative, 0 or null.",
                errorExample(HttpStatus.BAD_REQUEST, url,
                        "Id cannot be null", "id", "Id cannot be null")
        );
    }

    private static Example ex401(String url) {
        return ex(
                "User unauthorized.",
                "User unauthorized.",
                errorExample(HttpStatus.UNAUTHORIZED, url,
                        "Token is invalid."));
    }

    private static Example ex500(String url) {
        return ex(
                "Temporary service error.",
                "Temporary service error.",
                errorExample(HttpStatus.INTERNAL_SERVER_ERROR, url,
                        "Temporary service error."));
    }

    private static Example ex503(String url) {
        return ex(
                "Service unavailable.",
                "Service unavailable.",
                errorExample(HttpStatus.SERVICE_UNAVAILABLE, url,
                        "The server is currently overloaded or under maintenance. Please try again later."));
    }

    private static Map<String, Object> errorExample(HttpStatus status,
                                                    String path,
                                                    String topMessage) {
        return errorExample(status, path, topMessage, null, null);
    }

    private static Map<String, Object> errorExample(HttpStatus status,
                                                    String path,
                                                    String topMessage,
                                                    String field,
                                                    String fieldMessage) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", TIMESTAMP_EXAMPLE);
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", List.of(topMessage));
        body.put("path", path);

        if (field != null && fieldMessage != null) {
            Map<String, Object> ve = new LinkedHashMap<>();
            ve.put("field", field);
            ve.put("message", fieldMessage);
            body.put("validationErrors", List.of(ve));
        } else {
            body.put("validationErrors", null);
        }
        return body;
    }

    /**
     * Defines the standard HTTP Bearer security scheme (bearerAuth).
     */
    private static SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("UUID");
    }
}
