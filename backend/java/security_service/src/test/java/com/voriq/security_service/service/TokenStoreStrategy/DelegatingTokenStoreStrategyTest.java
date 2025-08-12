package com.voriq.security_service.service.TokenStoreStrategy;

import org.junit.jupiter.api.*;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Token store strategy tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class DelegatingTokenStoreStrategyTest {

    @Autowired
    private DelegatingTokenStoreStrategy delegator;

    @MockitoBean
    private RedisTokenStoreStrategy redisStrategy;

    @MockitoBean
    private InMemoryTokenStoreStrategy inMemoryStrategy;

    @MockitoBean
    private TokenMigrationService migrationService;

    private static final UUID USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final String TEST_TOKEN_1 = "Test token 1";
    private static final String TEST_TOKEN_2 = "Test token 2";
    private static final String TEST_TOKEN_3 = "Test token 3";

    @BeforeEach
    void resetDelegatorState() {
        ReflectionTestUtils.setField(delegator, "lastActiveClass", null);
    }

    @AfterEach
    void tearDown() {
        reset(redisStrategy, inMemoryStrategy, migrationService);
    }

    @Test
    void saveToken_should_delegate_to_redis_when_available_and_no_migration_needed() {

        when(redisStrategy.isApplicable()).thenReturn(true);
        when(inMemoryStrategy.isApplicable()).thenReturn(true);

        when(migrationService.isInMemoryEmpty()).thenReturn(true);

        delegator.saveToken(TEST_TOKEN_1, USER_ID);

        verify(migrationService, times(1)).isInMemoryEmpty();
        verify(migrationService, never()).migrateToRedis();

        verify(redisStrategy, times(1)).saveToken(TEST_TOKEN_1, USER_ID);
        verify(inMemoryStrategy, never()).saveToken(any(), any());
    }

    @Test
    void saveToken_should_fallback_to_inmemory_when_redis_fails() {
        when(redisStrategy.isApplicable()).thenReturn(true);
        when(inMemoryStrategy.isApplicable()).thenReturn(true);
        when(migrationService.isInMemoryEmpty()).thenReturn(true);

        doThrow(new RuntimeException("Redis down"))
                .when(redisStrategy).saveToken(any(), any());

        assertDoesNotThrow(() -> delegator.saveToken(TEST_TOKEN_2, USER_ID));

        verify(inMemoryStrategy, times(1)).saveToken(TEST_TOKEN_2, USER_ID);
    }

    @Test
    void saveToken_should_trigger_migration_once_when_redis_becomes_active_and_inmemory_not_empty() {

        when(redisStrategy.isApplicable()).thenReturn(true);
        when(inMemoryStrategy.isApplicable()).thenReturn(true);

        doThrow(new RuntimeException("Redis down"))
                .when(redisStrategy).saveToken(any(), any());

        when(migrationService.isInMemoryEmpty()).thenReturn(true);

        delegator.saveToken(TEST_TOKEN_1, USER_ID);
        verify(inMemoryStrategy, times(1)).saveToken(TEST_TOKEN_1, USER_ID);

        reset(redisStrategy, inMemoryStrategy, migrationService);

        when(redisStrategy.isApplicable()).thenReturn(true);
        when(inMemoryStrategy.isApplicable()).thenReturn(true);

        when(migrationService.isInMemoryEmpty()).thenReturn(false);

        delegator.saveToken(TEST_TOKEN_2, USER_ID);

        InOrder inOrder = inOrder(migrationService, redisStrategy);
        inOrder.verify(migrationService, times(1)).isInMemoryEmpty();
        inOrder.verify(migrationService, times(1)).migrateToRedis();
        inOrder.verify(redisStrategy, times(1)).saveToken(TEST_TOKEN_2, USER_ID);

        reset(migrationService);
        when(migrationService.isInMemoryEmpty()).thenReturn(false);

        delegator.saveToken(TEST_TOKEN_3, USER_ID);

        verify(migrationService, never()).migrateToRedis(); // не повторяем
        verify(redisStrategy, times(1)).saveToken(TEST_TOKEN_3, USER_ID);
    }

    @Test
    void isValid_should_fallback_and_then_migrate_on_redis_recovery() {

        when(redisStrategy.isApplicable()).thenReturn(true);
        when(inMemoryStrategy.isApplicable()).thenReturn(true);
        doThrow(new RuntimeException("Redis down")).when(redisStrategy).isValid(any());

        when(inMemoryStrategy.isApplicable()).thenReturn(true);
        when(inMemoryStrategy.isValid(TEST_TOKEN_1)).thenReturn(true);

        assertTrue(delegator.isValid(TEST_TOKEN_1));
        verify(inMemoryStrategy, times(1)).isValid(TEST_TOKEN_1);

        reset(redisStrategy, inMemoryStrategy, migrationService);

        when(redisStrategy.isApplicable()).thenReturn(true);
        when(redisStrategy.isValid(TEST_TOKEN_1)).thenReturn(true);
        when(inMemoryStrategy.isApplicable()).thenReturn(true);

        when(migrationService.isInMemoryEmpty()).thenReturn(false);

        assertTrue(delegator.isValid(TEST_TOKEN_1));

        InOrder inOrder = inOrder(migrationService, redisStrategy);
        inOrder.verify(migrationService, times(1)).isInMemoryEmpty();
        inOrder.verify(migrationService, times(1)).migrateToRedis();
        inOrder.verify(redisStrategy, times(1)).isValid(TEST_TOKEN_1);
    }
}
