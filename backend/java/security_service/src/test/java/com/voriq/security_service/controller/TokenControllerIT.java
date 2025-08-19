package com.voriq.security_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.domain.dto.TokensDto;
import com.voriq.security_service.domain.entity.User;
import com.voriq.security_service.exception_handler.dto.ErrorResponse;
import com.voriq.security_service.exception_handler.exception.ServerException;
import com.voriq.security_service.repository.UserRepository;
import com.voriq.security_service.service.TokenServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.voriq.security_service.config.SecurityConfig.*;
import static com.voriq.security_service.service.TokenStoreStrategy.DelegatingTokenStoreStrategy.DEFAULT_SET_VALUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test_utils.LogTestUtils.getLastLineFromLog;
import static test_utils.LogTestUtils.removeLastLogLine;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Token controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class TokenControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockitoSpyBean
    private TokenServiceImpl tokenService;

    @Value("${prefix.blocked}")
    private String blockedPrefix;

    @Value("${rate.limit-ms.issue}")
    private long issueRequestLimitIntervalMs;

    @Value("${rate.limit-ms.validate}")
    private long validateRequestLimitIntervalMs;

    @Value("${log.dir}")
    private String logDir;

    private static final UUID USER_ID_1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID USER_KEY_1 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    private static final UUID USER_ID_2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID USER_KEY_2 = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    @BeforeAll
    void setUp() throws IOException {
        User user = User.builder()
                .userId(USER_ID_1)
                .key(USER_KEY_1)
                .build();

        userRepository.save(user);
    }

    private void clearRedis(UUID userId, Set<String> tokens) {
        redisTemplate.delete(userId.toString());
        tokens.forEach(t -> redisTemplate.delete(t));
    }

    private String getNewToken(UUID userId, UUID userKey, boolean isWaiting) throws Exception {
        TokenRequestDto dto = TokenRequestDto.builder()
                .userId(userId)
                .key(userKey)
                .build();

        String dtoJson = mapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(post(ISSUE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        TokensDto responseDto = mapper.readValue(jsonResponse, TokensDto.class);

        if (isWaiting) waitForRateLimitReset();
        return responseDto.getAccessToken();
    }

    private String getNewToken(UUID userId, UUID userKey) throws Exception {
        return getNewToken(userId, userKey, true);
    }

    private String getNewToken() throws Exception {
        return getNewToken(USER_ID_1, USER_KEY_1);
    }

    private void waitForRateLimitReset(long limit) {
        try {
            Thread.sleep(limit + 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void waitForRateLimitReset() {
        waitForRateLimitReset(issueRequestLimitIntervalMs);
    }

    @Nested
    @DisplayName("POST /api" + ISSUE_URL)
    class IssueTokenTest {

        @Test
        public void issue_token_should_return_200() throws Exception {
            String token = getNewToken();
            String userId = USER_ID_1.toString();

            Set<String> redisTokenValue = redisTemplate.opsForSet().members(token);
            assertTrue(redisTokenValue != null && redisTokenValue.contains(userId));

            Set<String> values = redisTemplate.opsForSet().members(userId);
            assertTrue(values != null && values.contains(token));

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[INFO]"));
            assertTrue(last.contains(USER_ID_1.toString()));
            assertTrue(last.contains("User with ID"));
            assertTrue(last.contains("Code= 200"));

            clearRedis(USER_ID_1, Collections.singleton(token));
            removeLastLogLine();
        }

        @ParameterizedTest(name = "Test {index}: issue_token_should_return_400_when_request_data_isnt_valid [{arguments}]")
        @MethodSource("wrongUserData")
        public void issue_token_should_return_400_when_request_data_isnt_valid(String id, String key) throws Exception {
            String requestJson = """
                    {
                        "userId": %s,
                        "key": %s
                    }
                    """.formatted(
                    id == null ? null : "\"" + id + "\"",
                    key == null ? null : "\"" + key + "\""
            );

            MvcResult result = mockMvc.perform(post(ISSUE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(id == null || last.contains(id));
            assertTrue(last.contains("User with ID"));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        private static Stream<Arguments> wrongUserData() {
            return Stream.of(
                    Arguments.of("Test1", USER_KEY_1.toString()),
                    Arguments.of(USER_ID_1.toString(), "test2"),
                    Arguments.of(null, USER_KEY_1.toString()),
                    Arguments.of(USER_ID_1.toString(), null),
                    Arguments.of(null, null)
            );
        }

        @Test
        public void issue_token_should_return_403_when_user_is_blocked() throws Exception {
            Set<String> tokens = new HashSet<>();

            for (int i = 0; i < 4; i++) {
                tokens.add(getNewToken());
                removeLastLogLine();
            }

            TokenRequestDto dto = TokenRequestDto.builder()
                    .userId(USER_ID_1)
                    .key(USER_KEY_1)
                    .build();

            String dtoJson = mapper.writeValueAsString(dto);

            MvcResult result = mockMvc.perform(post(ISSUE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isForbidden())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.FORBIDDEN.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(USER_ID_1.toString()));
            assertTrue(last.contains("User with ID"));
            assertTrue(last.contains("Code= 403"));

            removeLastLogLine();
            clearRedis(USER_ID_1, tokens);
            redisTemplate.delete(blockedPrefix + USER_ID_1);
        }

        @ParameterizedTest(name = "Test {index}: issue_token_should_return_404_when_user_not_found [{arguments}]")
        @MethodSource("incorrectUserData")
        public void issue_token_should_return_404_when_user_not_found(TokenRequestDto dto) throws Exception {

            String dtoJson = mapper.writeValueAsString(dto);

            MvcResult result = mockMvc.perform(post(ISSUE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.NOT_FOUND.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(dto.getUserId().toString()));
            assertTrue(last.contains("User with ID"));
            assertTrue(last.contains("Code= 404"));

            redisTemplate.delete(blockedPrefix + dto.getUserId());
            removeLastLogLine();
            waitForRateLimitReset();
        }

        private static Stream<TokenRequestDto> incorrectUserData() {
            return Stream.of(
                    TokenRequestDto.builder().userId(USER_ID_2).key(USER_KEY_2).build(),
                    TokenRequestDto.builder().userId(USER_ID_2).key(USER_KEY_1).build(),
                    TokenRequestDto.builder().userId(USER_ID_1).key(USER_KEY_2).build()
            );
        }

        @Test
        public void issue_token_should_return_429_when_too_many_requests_from_user() throws Exception {

            String token = getNewToken(USER_ID_1, USER_KEY_1, false);
            removeLastLogLine();
            TokenRequestDto dto = TokenRequestDto.builder()
                    .userId(USER_ID_1)
                    .key(USER_KEY_1)
                    .build();

            String dtoJson = mapper.writeValueAsString(dto);

            for (int i = 0; i < 3; i++) {
                MvcResult result = mockMvc.perform(post(ISSUE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dtoJson))
                        .andExpect(status().isTooManyRequests())
                        .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();
                ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

                assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), responseDto.getStatus());
                String last = getLastLineFromLog();

                assertNotNull(last);
                assertTrue(last.contains("[ERROR]"));
                assertTrue(last.contains(USER_ID_1.toString()));
                assertTrue(last.contains("User with ID"));
                assertTrue(last.contains("Code= 429"));

                removeLastLogLine();
            }
            clearRedis(USER_ID_1, Collections.singleton(token));
        }

        @Test
        void issue_token_should_return_500_when_service_throws_exception() throws Exception {
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(tokenService).createTokens(any());

                TokenRequestDto dto = TokenRequestDto.builder()
                        .userId(USER_ID_1)
                        .key(USER_KEY_1)
                        .build();

                String dtoJson = mapper.writeValueAsString(dto);

                MvcResult result = mockMvc.perform(post(ISSUE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dtoJson))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();
                ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseDto.getStatus());
                String last = getLastLineFromLog();

                assertNotNull(last);
                assertTrue(last.contains("[ERROR]"));
                assertTrue(last.contains(USER_ID_1.toString()));
                assertTrue(last.contains("User with ID"));
                assertTrue(last.contains("Code= 500"));

                removeLastLogLine();
                waitForRateLimitReset();
            } finally {
                reset(tokenService);
            }
        }

        @Test
        void issue_token_should_return_503_when_database_unavailable() throws Exception {
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(userRepository).findKeyOnlyByUserId(any());

                TokenRequestDto dto = TokenRequestDto.builder()
                        .userId(USER_ID_1)
                        .key(USER_KEY_1)
                        .build();

                String dtoJson = mapper.writeValueAsString(dto);

                MvcResult result = mockMvc.perform(post(ISSUE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dtoJson))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();
                ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

                assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), responseDto.getStatus());
                String last = getLastLineFromLog();

                assertNotNull(last);
                assertTrue(last.contains("[ERROR]"));
                assertTrue(last.contains(USER_ID_1.toString()));
                assertTrue(last.contains("User with ID"));
                assertTrue(last.contains("Code= 503"));

                removeLastLogLine();
                waitForRateLimitReset();
            } finally {
                reset(userRepository);
            }
        }
    }

    @Nested
    @DisplayName("GET /api" + VALIDATE_URL)
    class ValidateTokenTest {

        @Test
        public void validate_token_should_return_204() throws Exception {
            String token = getNewToken();
            removeLastLogLine();

            mockMvc.perform(get(VALIDATE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[INFO]"));
            assertTrue(last.contains("Code= 204"));
            assertTrue(last.contains("User with ID"));
            assertTrue(last.contains(USER_ID_1.toString()));
            assertTrue(last.contains(token.substring(token.length() - 3)));

            clearRedis(USER_ID_1, Collections.singleton(token));
            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void validate_token_should_return_400_when_token_is_wrong() throws Exception {
            String token = "test1";
            MvcResult result = mockMvc.perform(get(VALIDATE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void validate_token_should_return_400_when_token_is_null() throws Exception {
            MvcResult result = mockMvc.perform(get(VALIDATE_URL))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void validate_token_should_return_400_when_header_authorization_is_not_bearer() throws Exception {
            String token = "test1";
            MvcResult result = mockMvc.perform(get(VALIDATE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "TEst " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void validate_token_should_return_401_when_token_isnt_valid() throws Exception {

            mockMvc.perform(get(VALIDATE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 401"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void validate_token_should_return_429__when_too_many_requests() throws Exception {

            String token = getNewToken(USER_ID_1, USER_KEY_1, false);
            removeLastLogLine();
            waitForRateLimitReset();
            mockMvc.perform(get(VALIDATE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());
            removeLastLogLine();
            for (int i = 0; i < 3; i++) {
                MvcResult result = mockMvc.perform(get(VALIDATE_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                        .andExpect(status().isTooManyRequests())
                        .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();
                ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

                assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), responseDto.getStatus());
                String last = getLastLineFromLog();

                assertNotNull(last);
                assertTrue(last.contains("[ERROR]"));
                assertTrue(last.contains("User with ID"));
                assertTrue(last.contains("Code= 429"));

                removeLastLogLine();
            }
            clearRedis(USER_ID_1, Collections.singleton(token));
            waitForRateLimitReset();
        }

        @Test
        void validate_token_should_return_500_when_service_throws_exception() throws Exception {
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(tokenService).validateToken(any());
                waitForRateLimitReset(issueRequestLimitIntervalMs);
                String token = getNewToken();
                removeLastLogLine();
                waitForRateLimitReset(issueRequestLimitIntervalMs);
                MvcResult result = mockMvc.perform(get(VALIDATE_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();
                ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseDto.getStatus());
                String last = getLastLineFromLog();

                assertNotNull(last);
                assertTrue(last.contains("[ERROR]"));
                assertTrue(last.contains("User with ID"));
                assertTrue(last.contains("Code= 500"));

                clearRedis(USER_ID_1, Collections.singleton(token));

                removeLastLogLine();

            } finally {
                reset(tokenService);
                waitForRateLimitReset();
            }
        }
    }

    @Nested
    @DisplayName("DELETE: /api" + REVOKE_URL)
    class RevokeTokenTest {

        @Test
        public void revoke_token_should_return_204() throws Exception {
            String token = getNewToken();
            removeLastLogLine();

            mockMvc.perform(delete(REVOKE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[INFO]"));
            assertTrue(last.contains("Code= 204"));
            assertTrue(last.contains("Token"));
            assertTrue(last.contains(token.substring(token.length() - 3)));

            assertNotEquals(Boolean.TRUE, redisTemplate.hasKey(token));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void revoke_token_should_return_400_when_token_is_wrong() throws Exception {
            String token = "test1";
            MvcResult result = mockMvc.perform(delete(REVOKE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void revoke_token_should_return_400_when_token_is_null() throws Exception {
            MvcResult result = mockMvc.perform(delete(REVOKE_URL))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void revoke_token_should_return_400_when_header_authorization_is_not_bearer() throws Exception {
            String token = "test1";
            MvcResult result = mockMvc.perform(delete(REVOKE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "TEst " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST.value(), responseDto.getStatus());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 400"));

            removeLastLogLine();
            waitForRateLimitReset();
        }

        @Test
        public void revoke_token_should_return_401_when_token_isnt_valid() throws Exception {

            mockMvc.perform(delete(REVOKE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());

            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains(DEFAULT_SET_VALUE));
            assertTrue(last.contains("Code= 401"));

            removeLastLogLine();
            waitForRateLimitReset();
        }
    }

    @Test
    void revoke_token_should_return_500_when_service_throws_exception() throws Exception {
        try {
            doThrow(new ServerException("The token could not be revoked. Try again later."))
                    .when(tokenService).revokeToken(any());
            waitForRateLimitReset(issueRequestLimitIntervalMs);
            String token = getNewToken();
            removeLastLogLine();
            waitForRateLimitReset(issueRequestLimitIntervalMs);
            MvcResult result = mockMvc.perform(delete(REVOKE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            ErrorResponse responseDto = mapper.readValue(jsonResponse, ErrorResponse.class);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseDto.getStatus());
            String last = getLastLineFromLog();

            assertNotNull(last);
            assertTrue(last.contains("[ERROR]"));
            assertTrue(last.contains("User with ID"));
            assertTrue(last.contains("Code= 500"));

            clearRedis(USER_ID_1, Collections.singleton(token));

            removeLastLogLine();

        } finally {
            reset(tokenService);
            waitForRateLimitReset();
        }
    }
}