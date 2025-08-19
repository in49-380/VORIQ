package com.voriq.security_service.config;

import com.voriq.security_service.config.configs_components.CustomAccessDeniedHandler;
import com.voriq.security_service.config.configs_components.CustomAuthenticationEntryPoint;
import com.voriq.security_service.filter.TokenRateLimitFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

import java.time.Clock;
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
 *   <li>Registers {@link TokenRateLimitFilter} <em>before</em> {@link UsernamePasswordAuthenticationFilter}
 *       to enforce per-user rate limiting prior to any authentication logic.</li>
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

    private final TokenRateLimitFilter tokenRateLimitFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Relative URL for issuing tokens (mounted under the global context path, e.g. "/api").
     */
    public static final String ISSUE_URL = "/v1/tokens/issue";
    /**
     * Relative URL for validating tokens (mounted under the global context path, e.g. "/api").
     */
    public static final String VALIDATE_URL = "/v1/tokens/validate";
    /**
     * Relative URL for revoking tokens (mounted under the global context path, e.g. "/api").
     */
    public static final String REVOKE_URL = "/v1/tokens/revoke";

    /**
     * Builds the main Spring Security filter chain.
     *
     * <p><strong>Defaults:</strong></p>
     * <ul>
     *   <li>CSRF is disabled (stateless token-based API).</li>
     *   <li>CORS is enabled with default settings ({@code cors(withDefaults())}).</li>
     *   <li>Sessions are {@link org.springframework.security.config.http.SessionCreationPolicy#STATELESS STATELESS}.</li>
     *   <li>HTTP Basic is disabled.</li>
     * </ul>
     *
     * <p><strong>Authorization rules:</strong></p>
     * <ul>
     *   <li>Anonymous access is allowed to:
     *     <ul>
     *       <li>Swagger/OpenAPI: {@code /swagger-ui/**}, {@code /v3/api-docs/**}, {@code /swagger-ui.html}</li>
     *       <li>Error page: {@code /error}</li>
     *       <li>{@link #ISSUE_URL} — {@code POST} only</li>
     *       <li>{@link #VALIDATE_URL} — {@code GET} only</li>
     *       <li>{@link #REVOKE_URL} — {@code DELETE} only</li>
     *     </ul>
     *   </li>
     *   <li>All other requests require authentication.</li>
     * </ul>
     *
     * <p><strong>Exception handling:</strong> Custom handlers are configured:
     * {@code customAuthenticationEntryPoint} for 401 (unauthenticated) and
     * {@code customAccessDeniedHandler} for 403 (forbidden).</p>
     *
     * <p><strong>Filter ordering:</strong> {@code tokenRateLimitFilter} is added
     * <em>before</em> {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
     * so rate limiting is enforced before authentication components read the request body. This prevents unnecessary
     * authentication work and allows early rejection of bursty traffic to token endpoints.</p>
     *
     * <p><strong>Extensibility notes:</strong></p>
     * <ul>
     *   <li>When adding new public endpoints, explicitly permit them here (method + path) and update this Javadoc.</li>
     *   <li>If you switch away from stateless tokens (e.g., form login), revisit CSRF and HTTP Basic settings.</li>
     *   <li>If {@code tokenRateLimitFilter} logic changes, ensure it still runs before authentication filters.</li>
     * </ul>
     *
     * @param http the {@link org.springframework.security.config.annotation.web.builders.HttpSecurity} builder
     * @return the configured {@link org.springframework.security.web.SecurityFilterChain}
     * @throws Exception if the chain cannot be built
     */

    @Bean
    public SecurityFilterChain configureAuth(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, ISSUE_URL).permitAll()
                        .requestMatchers(HttpMethod.GET, VALIDATE_URL).permitAll()
                        .requestMatchers(HttpMethod.DELETE, REVOKE_URL).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // Ensure rate-limit runs early, before auth processing reads the request body
                .addFilterBefore(tokenRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
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
     * Defines the Bearer HTTP security scheme (JWT).
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    /**
     * CORS configuration built from {@code cors.allowed-origins}.
     *
     * <p>Allowed methods: GET, POST, PUT, DELETE, OPTIONS. Headers: *</p>
     *
     * @return source mapping all paths to the configured CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> allowedOriginsList = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(allowedOriginsList);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
