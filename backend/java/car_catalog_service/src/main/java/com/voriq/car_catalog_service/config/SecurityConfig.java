package com.voriq.car_catalog_service.config;

import com.voriq.car_catalog_service.config.configs_components.CustomAccessDeniedHandler;
import com.voriq.car_catalog_service.config.configs_components.CustomAuthenticationEntryPoint;
import com.voriq.car_catalog_service.filter.TmpTokenAuthFilter;
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

import java.util.Arrays;
import java.util.List;

import static com.voriq.car_catalog_service.config.ApiPaths.*;
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
    private final TmpTokenAuthFilter tmpTokenAuthFilter;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Builds and wires the primary {@link SecurityFilterChain}.
     * <p>Key settings:</p>
     * <ul>
     *   <li>Disables CSRF and HTTP sessions (stateless via {@link SessionCreationPolicy#STATELESS}).</li>
     *   <li>Enables CORS using {@link #corsConfigurationSource()}.</li>
     *   <li>Permits Swagger/OpenAPI endpoints and {@code /error}.</li>
     *   <li>Requires authentication for catalog read endpoints (GET) and resolve (POST).</li>
     *   <li>Permits {@code GET}  for simple availability tests.</li>
     *   <li>Denies all other requests.</li>
     *   <li>Registers {@link TmpTokenAuthFilter} before {@link UsernamePasswordAuthenticationFilter}.</li>
     *   <li>Configures custom {@link CustomAuthenticationEntryPoint} and
     *       {@link CustomAccessDeniedHandler} for error handling.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if the security chain cannot be built
     * @since 1.0.0
     * @author RsLan
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

                        .requestMatchers(
                                HttpMethod.GET,
                                BRANDS_URL,
                                MODELS_URL,
                                YEARS_URL,
                                ENGINES_URL,
                                TRANSMISSIONS_URL,
                                WHEEL_DRIVES_URL
                        ).authenticated()
                        .requestMatchers(HttpMethod.POST, RESOLVE_URL).authenticated()

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
     * @since 1.0.0
     * @author RsLan
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the Bearer HTTP security scheme (UUID).
     *
     * @since 1.0.0
     * @author RsLan
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
     * @since 1.0.0
     * @author RsLan
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
