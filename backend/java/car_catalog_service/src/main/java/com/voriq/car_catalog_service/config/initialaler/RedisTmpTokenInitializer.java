package com.voriq.car_catalog_service.config.initialaler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Profile({"dev", "test"})
@Slf4j
public class RedisTmpTokenInitializer {

    private final StringRedisTemplate redisTemplate;

    private static final String prefixOld = "tmp-token:";

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

            removeOldTmpToken(redisTemplate, prefix);

            redisTemplate.opsForValue().set(prefix + tmpToken1, "Joe Biden");
            redisTemplate.opsForValue().set(prefix + tmpToken2, "Donald Trump");
            redisTemplate.opsForValue().set(prefix + tmpToken3, "Barack Obama");
            System.out.println("✅ Tokens in Redis initialized");
        };
    }

    public static void removeOldTmpToken(StringRedisTemplate redisTemplate, String prefix) {

        Set<String> keys = redisTemplate.keys(prefixOld + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Deleted {} Redis keys with prefix {}: ", keys.size(), prefix);
            keys.forEach(RedisTmpTokenInitializer::maskKey);
        } else {
            log.info("No Redis keys found with prefix {}", prefix);
        }
    }

    private static void maskKey(String fullKey) {
        String tail = fullKey.substring(prefixOld.length());
        String first4 = tail.substring(0, Math.min(4, tail.length()));
        log.info("- {}{}****", prefixOld, first4);
    }
}


