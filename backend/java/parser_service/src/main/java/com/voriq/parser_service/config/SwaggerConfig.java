package com.voriq.parser_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Parser Service",
                description = "Parser Service",
                version = "0.1.0",
                contact = @Contact(
                        name = "Oleksandr Harbuz",
                        email = "pamail08@gmail.com"
                )
        )
)

public class SwaggerConfig {
}
