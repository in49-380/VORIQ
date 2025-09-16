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
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofDays;

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

            final long daysOf100Year = 36_525L;

            getAllRedisKeys();

            removeOldTmpToken(redisTemplate, prefixOld);

            redisTemplate.opsForValue().set(prefix + tmpToken1, "Joe Biden", ofDays(daysOf100Year));
            redisTemplate.opsForValue().set(prefix + tmpToken2, "Donald Trump", ofDays(daysOf100Year));
            redisTemplate.opsForValue().set(prefix + tmpToken3, "Barack Obama", ofDays(daysOf100Year));
            System.out.println("✅ Tokens in Redis initialized");
        };
    }

    public static void removeOldTmpToken(StringRedisTemplate redisTemplate, String prefix) {

        Set<String> keys = redisTemplate.keys(prefix + "*");
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

    private String maskKey20Char(String str) {
        if (str.isEmpty()) return str;
        return str.substring(0, Math.min(20, str.length())) + "****";
    }

    private void getAllRedisKeys() {
        Set<String> keys = redisTemplate.keys("*");

        keys.forEach(k -> {
                    Long ttl = redisTemplate.getExpire(k, TimeUnit.DAYS);
                    log.info("✅✅ Redis contains the following entries: key-{} expire-{}",
                            maskKey20Char(k), ttl);
                }
        );
    }
}


