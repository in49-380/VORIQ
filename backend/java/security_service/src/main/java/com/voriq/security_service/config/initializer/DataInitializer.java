package com.voriq.security_service.config.initializer;

import com.voriq.security_service.domain.entity.User;
import com.voriq.security_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        UUID userId1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID key1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        UUID userId2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID key2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        if (!userRepository.existsByUserId(userId1)) {
            userRepository.save(User.builder()
                    .userId(userId1)
                    .key(key1)
                    .build());
        }

        if (!userRepository.existsByUserId(userId2)) {
            userRepository.save(User.builder()
                    .userId(userId2)
                    .key(key2)
                    .build());
        }
        System.out.println("✅ Test users added to the base");
    }
}

