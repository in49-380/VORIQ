package com.voriq.security_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Swagger / OpenAPI configuration holder.
 *
 * <p>This class holds the {@link OpenAPIDefinition} annotation that defines
 * the API metadata (title, description, version). Springdoc scans the classpath
 * and automatically exposes the OpenAPI spec and Swagger UI:
 * <ul>
 *   <li>Swagger UI: <code>/api/swagger-ui/index.html</code></li>
 *   <li>OpenAPI JSON: <code>/api/v3/api-docs</code></li>
 * </ul>
 * The base context path (<code>/api</code>) is configured at the application level.</p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "VORIQ: security service",
                description = "API for issuing, validating, and revoking access tokens used for user authentication and session management.",
                version = "1.0.0"
        )
)
public class SwaggerConfig {
}
