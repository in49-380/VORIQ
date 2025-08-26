package com.voriq.car_catalog_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
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
                title = "VORIQ: car catalog service",
                description = "API for obtaining information on a car in the catalog for an authorized user.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Ruslan Senkin",
                        email = "rsenkin24@gmail.com"
                )
        )
)
public class SwaggerConfig {
}
