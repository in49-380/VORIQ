package com.voriq.security_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "VORIQ: security service",
                description = "API for issuing, validating, and revoking JWT access tokens used for user authentication and session management.",
                version = "1.0.0"
        )
)
public class SwaggerConfig {
}
