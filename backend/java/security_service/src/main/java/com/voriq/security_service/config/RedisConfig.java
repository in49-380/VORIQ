package com.voriq.security_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis configuration.
 *
 * <p>Provides a {@link StringRedisTemplate} backed by the auto-configured
 * {@link LettuceConnectionFactory}. Connection settings (host, port, password, etc.)
 * are taken from standard Spring properties:
 * <ul>
 *   <li>{@code spring.data.redis.host}</li>
 *   <li>{@code spring.data.redis.port}</li>
 *   <li>{@code spring.data.redis.password} (optional)</li>
 *   <li>…and other Lettuce-related properties</li>
 * </ul>
 * </p>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li>Bean is thread-safe and intended to be reused across the application.</li>
 *   <li>Connection pooling/timeouts are managed by Lettuce; tune via Spring properties if needed.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a {@link StringRedisTemplate} using the provided {@link LettuceConnectionFactory}.
     *
     * @param connectionFactory Lettuce connection factory (auto-configured by Spring Boot)
     * @return a singleton {@code StringRedisTemplate} bean
     */
    @Bean
    public StringRedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
