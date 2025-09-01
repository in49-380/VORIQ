package com.voriq.car_catalog_service.config;

import com.voriq.car_catalog_service.config.configs_components.CustomAccessDeniedHandler;
import com.voriq.car_catalog_service.config.configs_components.CustomAuthenticationEntryPoint;
import com.voriq.car_catalog_service.filter.TmpTokenAuthFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security and OpenAPI configuration for the Security Service.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Stateless security (no HTTP sessions).</li>
 *   <li>Permits Swagger & OpenAPI endpoints and the token issue endpoint.</li>
 *   <li>Configures CORS from the {@code cors.allowed-origins} property.</li>
 *   <li>Sets up OpenAPI with Bearer authentication scheme.</li>
 * </ul>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li>Allowed origins must be provided as a comma-separated list in {@code cors.allowed-origins}.</li>
 *   <li>If you introduce protected endpoints, extend the authorization rules accordingly.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final TmpTokenAuthFilter tmpTokenAuthFilter ;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    public static final String BRANDS_URL = "/v1/brands";

    public static final String ENGINES_URL = "/v1/engines";

    public static final String FUEL_TYPE_URL = "/v1/fuel-types";

    public static final String CAR_ALL_URL = "/v1/cars";

    public static final String CAR_ID_URL = "/v1/cars/by-id/{id}";

    public static final String MODEL_BRAND_URL = "/v1/models/by-brand/{brand}";

    public static final String YEAR_URL = "/v1/years";

    public static final String TEST_DELAY_URL = "/v1/test/delay-ms";

    @Bean
    public SecurityFilterChain configureAuth(HttpSecurity http) throws Exception {



        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, BRANDS_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, ENGINES_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, FUEL_TYPE_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, CAR_ALL_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, CAR_ID_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, MODEL_BRAND_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, YEAR_URL).authenticated()
                        .requestMatchers(HttpMethod.GET, TEST_DELAY_URL).permitAll()
                        .anyRequest().denyAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                          .addFilterBefore(tmpTokenAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Exposes the {@link AuthenticationManager} from Spring Boot's {@link AuthenticationConfiguration}.
     *
     * @param config boot-managed authentication configuration
     * @return the {@link AuthenticationManager}
     * @throws Exception if the manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures OpenAPI with a Bearer (JWT) security scheme and a global security requirement.
     *
     * @return initialized {@link OpenAPI} bean
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    /**
     * Defines the Bearer HTTP security scheme (UUID).
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("UUID")
                .scheme("bearer");
    }

    /**
     * CORS configuration built from {@code cors.allowed-origins}.
     *
     * <p>Allowed methods: GET. Headers: *</p>
     *
     * @return source mapping all paths to the configured CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> allowedOriginsList = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(allowedOriginsList);
        config.setAllowedMethods(List.of("GET"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
