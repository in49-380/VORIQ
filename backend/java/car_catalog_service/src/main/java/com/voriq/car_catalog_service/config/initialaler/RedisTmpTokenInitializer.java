package com.voriq.car_catalog_service.config.initialaler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class RedisTmpTokenInitializer {

    private final StringRedisTemplate redisTemplate;

    @Value("${tmp-token.1}")
    private String tmpToken1;

    @Value("${tmp-token.2}")
    private String tmpToken2;

    @Value("${tmp-token.3}")
    private String tmpToken3;

    @Value("${tmp-token.prefix}")
    private String prefix;

    @Bean
    public ApplicationRunner initTmpTokens() {
        return args -> {
            redisTemplate.opsForValue().set(prefix + tmpToken1, "Joe Biden");
            redisTemplate.opsForValue().set(prefix + tmpToken2, "Donald Trump");
            redisTemplate.opsForValue().set(prefix + tmpToken3, "Barack Obama");
        };
    }
}


