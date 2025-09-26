package com.voriq.security_service.config;

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

import static com.voriq.security_service.config.ApiPaths.*;

/**
 * Centralized OpenAPI components: security scheme and reusable error examples.
 */
@Configuration
public class OpenApiExamplesConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";
    private static final String TIMESTAMP_EXAMPLE = "2025-07-21T11:20:00Z"; // ISO-8601

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components()
                .addSecuritySchemes(BEARER_SCHEME_NAME, bearerScheme())

                // 400
                .addExamples("Error400MissingUserId", ex(
                        "UserId is wrong.",
                        "User ID is either null or not in UUID format.",
                        errorExample(HttpStatus.BAD_REQUEST, ISSUE_URL,
                                "User id cannot be null", "userId", "User id cannot be null")
                ))
                .addExamples("Error400MissingUserKey", ex(
                        "User key is wrong.",
                        "User key is either null or not in UUID format.",
                        errorExample(HttpStatus.BAD_REQUEST, ISSUE_URL,
                                "User key cannot be null", "userKey", "User key cannot be null")
                ))
                .addExamples("Error400MissingTokenValidate", ex(
                        "Token is wrong.",
                        "Token is not in UUID format.",
                        errorExample(HttpStatus.BAD_REQUEST, VALIDATE_URL,
                                "Token format is wrong.")
                ))
                .addExamples("Error400TokenIsNullValidate", ex(
                        "Token is null.",
                        "Bearer token is null.",
                        errorExample(HttpStatus.BAD_REQUEST, VALIDATE_URL,
                                "Bearer token is null.", "null", "Bearer token is null.")
                ))
                .addExamples("Error400MissingTokenRevoke", ex(
                        "Token is wrong.",
                        "Token is not in UUID format.",
                        errorExample(HttpStatus.BAD_REQUEST, REVOKE_URL,
                                "Token format is wrong.")
                ))
                .addExamples("Error400TokenIsNullRevoke", ex(
                        "Token is null.",
                        "Bearer token is null.",
                        errorExample(HttpStatus.BAD_REQUEST, REVOKE_URL,
                                "Bearer token is null.", "null", "Bearer token is null.")
                ))

                // 401
                .addExamples("Error401UserUnauthorizedValidate", ex(
                        "User unauthorized.",
                        "User unauthorized.",
                        errorExample(HttpStatus.UNAUTHORIZED, VALIDATE_URL,
                                "Token is invalid.")
                ))
                .addExamples("Error401UserUnauthorizedRevoke", ex(
                        "User unauthorized.",
                        "User unauthorized.",
                        errorExample(HttpStatus.UNAUTHORIZED, REVOKE_URL,
                                "Token is invalid.")
                ))

                // 403
                .addExamples("Error403UserDoesNotHaveAccess", ex(
                        "User does not have access.",
                        "Token is not in UUID format.",
                        errorExample(HttpStatus.FORBIDDEN, ISSUE_URL,
                                "The active session limit has been exceeded. New sessions are temporarily unavailable.")
                ))

                // 404
                .addExamples("Error404UserNotFound", ex(
                        "User not found.",
                        "User with Id and key not found.",
                        errorExample(HttpStatus.NOT_FOUND, ISSUE_URL,
                                "User with id <44444444-4444-4444-4444-444444444444> and key <********ddd> not found.")
                ))

                // 429
                .addExamples("Error429TooManyRequestsIssue", ex(
                        "Too many requests.",
                        "Too many requests. Try again later.",
                        errorExample(HttpStatus.TOO_MANY_REQUESTS, ISSUE_URL,
                                "Too many requests. Try again in 15 seconds.")
                ))
                .addExamples("Error429TooManyRequestsValidate", ex(
                        "Too many requests.",
                        "Too many requests. Try again later.",
                        errorExample(HttpStatus.TOO_MANY_REQUESTS, VALIDATE_URL,
                                "Too many requests. Try again in 15 seconds.")
                ))

                // 500
                .addExamples("Error500TemporaryServiceErrorIssue", ex(
                        "Temporary service error.",
                        "Temporary service error.",
                        errorExample(HttpStatus.INTERNAL_SERVER_ERROR, ISSUE_URL,
                                "Temporary service error.")
                ))
                .addExamples("Error500TemporaryServiceErrorValidate", ex(
                        "Temporary service error.",
                        "Temporary service error.",
                        errorExample(HttpStatus.INTERNAL_SERVER_ERROR, VALIDATE_URL,
                                "Temporary service error.")
                ))
                .addExamples("Error500TemporaryServiceErrorRevoke", ex(
                        "Temporary service error.",
                        "Temporary service error.",
                        errorExample(HttpStatus.INTERNAL_SERVER_ERROR, REVOKE_URL,
                                "Temporary service error.")
                ))

                // 503
                .addExamples("Error503ServiceUnavailable", ex(
                        "Service unavailable.",
                        "Service unavailable.",
                        errorExample(HttpStatus.SERVICE_UNAVAILABLE, ISSUE_URL,
                                "The server is currently overloaded or under maintenance. Please try again later.")
                ));

        return new OpenAPI()
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME));
    }

    private static Example ex(String summary, String description, Map<String, Object> value) {
        return new Example().summary(summary).description(description).value(value);
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
