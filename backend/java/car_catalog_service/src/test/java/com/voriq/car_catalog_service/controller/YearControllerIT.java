package com.voriq.car_catalog_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.car_catalog_service.domain.dto.FuelTypeResponseDto;
import com.voriq.car_catalog_service.domain.dto.YearResponseDto;
import com.voriq.car_catalog_service.domain.dto.abstracts.ItemResponseDto;
import com.voriq.car_catalog_service.exception_handler.dto.ErrorResponse;
import com.voriq.car_catalog_service.repository.YearJdbcRepository;
import com.voriq.car_catalog_service.service.YearServiceImp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.voriq.car_catalog_service.config.SecurityConfig.FUEL_TYPE_URL;
import static com.voriq.car_catalog_service.config.SecurityConfig.YEAR_URL;
import static com.voriq.car_catalog_service.config.initialaler.RedisTmpTokenInitializer.removeOldTmpToken;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Year controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class YearControllerIT {

    @Value("${tmp-token.1}")
    private String tmpToken1;

    @Value("${tmp-token.prefix}")
    private String prefix;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoSpyBean
    private YearServiceImp yearServiceImp;

    @MockitoSpyBean
    private YearJdbcRepository yearJdbcRepository;

    private ErrorResponse checkErrorResponseResult(
            MvcResult result,
            HttpStatus status,
            String url) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        ErrorResponse error = mapper.readValue(responseBody, ErrorResponse.class);
        assertNotNull(error.getMessage());
        assertEquals(error.getStatus(), status.value());
        assertEquals(error.getError(), status.getReasonPhrase());
        assertEquals(error.getPath(), url);

        return error;
    }

    @AfterAll
    void cleanRedis() {
        removeOldTmpToken(redisTemplate, prefix);
    }

    @Nested
    @DisplayName("GET: /api" + YEAR_URL)
    class getAllTests {

        @Test
        @Order(1)
        public void get_all_years_should_return_200() throws Exception {

            MvcResult result = mockMvc.perform(get(YEAR_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();

            ItemResponseDto<Integer> dto = mapper.readValue(responseBody, YearResponseDto.class);
                        assertNotNull(dto);
            assertNotNull(dto.getItems());
            dto.getItems().forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        @Sql(statements = {
                "SET REFERENTIAL_INTEGRITY FALSE;",
                "TRUNCATE TABLE cars;",
                "TRUNCATE TABLE models;",
                "TRUNCATE TABLE engines;",
                "TRUNCATE TABLE years;",
                "TRUNCATE TABLE brands;",
                "TRUNCATE TABLE fuel_types;",
                "SET REFERENTIAL_INTEGRITY TRUE;"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        public void get_all_years_when_empty_should_return_empty_list() throws Exception {
            mockMvc.perform(get(YEAR_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        @Order(3)
        public void get_all_years_should_return_401_token_is_incorrect() throws Exception {
            MvcResult result = mockMvc.perform(get(YEAR_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "wrong_token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, YEAR_URL);
        }

        @Test
        @Order(4)
        public void get_all_years_should_return_401_header_authorization_is_null() throws Exception {
            MvcResult result = mockMvc.perform(get(YEAR_URL))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, YEAR_URL);
        }

        @Test
        @Order(5)
        public void get_all_years_should_return_401_header_authorization_is_not_bearer() throws Exception {
            MvcResult result = mockMvc.perform(get(YEAR_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, YEAR_URL);
        }

        @Test
        @Order(6)
        void get_all_years_should_return_500_when_service_throws_exception() throws Exception {
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(yearServiceImp).getAll();

                MvcResult result = mockMvc.perform(get(YEAR_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR, YEAR_URL);
            } finally {
                reset(yearServiceImp);
            }
        }

        @Test
        @Order(7)
        void get_all_years_should_return_503_when_database_unavailable() throws Exception {
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(yearJdbcRepository).findAll();

                MvcResult result = mockMvc.perform(get(YEAR_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE, YEAR_URL);
            } finally {
                reset(yearJdbcRepository);
            }
        }
    }
}