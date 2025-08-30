package com.voriq.car_catalog_service.config.initialaler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

import static com.voriq.car_catalog_service.filter.TmpTokenAuthFilter.TMP_TOKEN_PREFIX;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class RedisTmpTokenInitializer {

    private final StringRedisTemplate redisTemplate;

    @Bean
    public ApplicationRunner initTmpTokens() {
        return args -> {
            redisTemplate.opsForValue().set(TMP_TOKEN_PREFIX+"6b5f3d92-4b8c-4f2a-9f88-1a6b2c8a1b1d", "Joe Biden");
            redisTemplate.opsForValue().set(TMP_TOKEN_PREFIX+"e8c1a3f5-27de-4f45-bc78-3a4b8f6d92d1", "Donald Trump");
            redisTemplate.opsForValue().set(TMP_TOKEN_PREFIX+"1f3d2a74-9c58-44de-92bb-6e1f2c8d8a7e", "Barack Obama");
        };
    }
}

