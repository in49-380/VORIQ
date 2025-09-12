package com.voriq.car_catalog_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;
import com.voriq.car_catalog_service.exception_handler.dto.ErrorResponse;
import com.voriq.car_catalog_service.repository.interfaces.CarCatalogRepository;
import com.voriq.car_catalog_service.service.CarCatalogServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.stream.Stream;

import static com.voriq.car_catalog_service.config.SecurityConfig.*;
import static com.voriq.car_catalog_service.config.initialaler.RedisTmpTokenInitializer.removeOldTmpToken;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Car catalog controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class CarCatalogLookupControllerIT {

    @Value("${tmp-token.1}")
    private String tmpToken1;

    @Value("${tmp-token.prefix}")
    private String prefix;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoSpyBean
    private CarCatalogRepository carCatalogRepository;

    @MockitoSpyBean
    private CarCatalogServiceImpl carCatalogService;

    @Autowired
    private StringRedisTemplate redisTemplate;

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

    private List<IdValueResponseDto> getBrands() throws Exception {

        MvcResult result = mockMvc.perform(get(CAR_CATALOG_BRANDS_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return mapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );
    }

    private Long getBrandId() throws Exception {
        List<IdValueResponseDto> dtoBrands = getBrands();
        return dtoBrands.get(0).getId();
    }

    //========================
    private List<IdValueResponseDto> getModelsByBrand(Long id) throws Exception {

        MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return mapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );
    }

    private List<IdValueResponseDto> getModelsByBrand() throws Exception {
        return getModelsByBrand(getBrandId());
    }

    private Long getModelId() throws Exception {
        return getModelsByBrand().get(0).getId();
    }

    //========================
    private List<IdValueResponseDto> getYears(Long brandId, Long modelId) throws Exception {

        MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return mapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );
    }

    private List<IdValueResponseDto> getYears() throws Exception {
        Long brandId = getBrandId();
        Long modelId = getModelId();
        return getYears(brandId, modelId);
    }

    private Long getYearId() throws Exception {
        return getYears().get(0).getId();
    }

    //========================
    private List<IdValueResponseDto> getEngines(Long brandId, Long modelId, Long yearId) throws Exception {

        MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return mapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );
    }

    private List<IdValueResponseDto> getEngines() throws Exception {
        Long brandId = getBrandId();
        Long modelId = getModelId();
        Long yearId = getYearId();
        return getEngines(brandId, modelId, yearId);
    }

    private Long getEngineId() throws Exception {
        return getEngines().get(0).getId();
    }

    //========================
    private List<IdValueResponseDto> getTransmissions(Long brandId,
                                                      Long modelId,
                                                      Long yearId,
                                                      Long engineId) throws Exception {

        MvcResult result = mockMvc.perform(get(
                        CAR_CATALOG_TRANSMISSIONS_URL,
                        brandId,
                        modelId,
                        yearId,
                        engineId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return mapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );
    }

    private List<IdValueResponseDto> getTransmissions() throws Exception {
        Long brandId = getBrandId();
        Long modelId = getModelId();
        Long yearId = getYearId();
        Long engineId = getEngineId();
        return getTransmissions(brandId, modelId, yearId, engineId);
    }

    private Long getTransmissionId() throws Exception {
        return getTransmissions().get(0).getId();
    }
    //========================

    private List<IdValueResponseDto> getWheelDrives(Long brandId,
                                                    Long modelId,
                                                    Long yearId,
                                                    Long engineId,
                                                    Long transmissionId) throws Exception {

        MvcResult result = mockMvc.perform(get(
                        CAR_CATALOG_WHEEL_DRIVES_URL,
                        brandId,
                        modelId,
                        yearId,
                        engineId,
                        transmissionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return mapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );
    }

    private List<IdValueResponseDto> getWheelDrives() throws Exception {
        Long brandId = getBrandId();
        Long modelId = getModelId();
        Long yearId = getYearId();
        Long engineId = getEngineId();
        Long transmissionId = getTransmissionId();
        return getWheelDrives(brandId, modelId, yearId, engineId, transmissionId);
    }

    private Long getWheelDriveId() throws Exception {
        return getWheelDrives().get(0).getId();
    }
    //========================


    @AfterAll
    void cleanRedis() {
        removeOldTmpToken(redisTemplate, prefix);
    }

    @Nested
    @DisplayName("GET: /api" + CAR_CATALOG_BRANDS_URL)
    class getALlBrandsTests {

        @Test
        @Order(1)
        public void get_all_brands_should_return_200() throws Exception {

            List<IdValueResponseDto> dto = getBrands();

            assertNotNull(dto);
            assertFalse(dto.isEmpty());
            dto.forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        public void get_all_brands_should_return_401_token_is_incorrect() throws Exception {

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_BRANDS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, CAR_CATALOG_BRANDS_URL);
        }

        @Test
        @Order(3)
        public void get_all_brands_should_return_401_header_authorization_is_null() throws Exception {

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_BRANDS_URL))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, CAR_CATALOG_BRANDS_URL);
        }

        @Test
        @Order(4)
        public void get_all_brands_should_return_401_header_authorization_is_not_bearer() throws Exception {

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_BRANDS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, CAR_CATALOG_BRANDS_URL);
        }

        @Test
        @Order(5)
        void get_all_brands_should_return_500_when_service_throws_exception() throws Exception {
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService).findALlBrands();

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_BRANDS_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR, CAR_CATALOG_BRANDS_URL);
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(6)
        void get_all_brands_should_return_503_when_database_unavailable() throws Exception {
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository).findALlBrands();

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_BRANDS_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE, CAR_CATALOG_BRANDS_URL);
            } finally {
                reset(carCatalogRepository);
            }
        }
    }

    @Nested
    @DisplayName("GET: /api" + CAR_CATALOG_MODELS_URL)
    class getModelsByBrandTests {


        private <B> String getURL(B brandId) {
            return new UriTemplate(CAR_CATALOG_MODELS_URL).expand(brandId).toString();
        }

        @Test
        @Order(1)
        public void get_models_by_brand_should_return_200() throws Exception {

            List<IdValueResponseDto> dto = getModelsByBrand();

            assertNotNull(dto);
            assertFalse(dto.isEmpty());
            dto.forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        public void get_models_by_brand_should_return_200_and_empty_list_when_brand_id_is_not_found() throws Exception {

            List<IdValueResponseDto> dto = getModelsByBrand(Long.MAX_VALUE);

            assertNotNull(dto);
            assertTrue(dto.isEmpty());
        }

        @Test
        @Order(3)
        public void get_models_by_brand_should_return_400_when_id_is_not_number() throws Exception {

            String brandId = "test";

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, getURL(brandId));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when brand id is wrong[{arguments}]")
        @CsvSource({
                "0",
                "-2938"
        })
        @Order(4)
        public void get_models_by_brand_should_return_400_when_id_is_not_positive(Long brandId) throws Exception {

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, getURL(brandId));
        }

        @Test
        @Order(5)
        public void get_models_by_brand_should_return_401_token_is_incorrect() throws Exception {

            Long brandId = getBrandId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId));
        }

        @Test
        @Order(6)
        public void get_models_by_brand_should_return_401_header_authorization_is_null() throws Exception {

            Long brandId = getBrandId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId));
        }

        @Test
        @Order(7)
        public void get_models_by_brand_should_return_401_header_authorization_is_not_bearer() throws Exception {

            Long brandId = getBrandId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId));
        }

        @Test
        @Order(8)
        void get_models_by_brand_return_500_when_service_throws_exception() throws Exception {
            Long brandId = getBrandId();
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService).findModelsByBrand(brandId);

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR, getURL(brandId));
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(9)
        void get_models_by_brand_should_return_503_when_database_unavailable() throws Exception {
            Long brandId = getBrandId();
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository).findModelsByBrand(brandId);

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_MODELS_URL, brandId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE, getURL(brandId));
            } finally {
                reset(carCatalogRepository);
            }
        }
    }

    @Nested
    @DisplayName("GET: /api" + CAR_CATALOG_YEARS_URL)
    class getYearsTests {

        private <B, M> String getURL(B brandId, M modelId) {
            return new UriTemplate(CAR_CATALOG_YEARS_URL).expand(brandId, modelId).toString();
        }

        @Test
        @Order(1)
        public void get_years_by_brand_id_and_model_id_should_return_200() throws Exception {

            List<IdValueResponseDto> dto = getYears();

            assertNotNull(dto);
            assertFalse(dto.isEmpty());
            dto.forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        public void get_years_by_brand_id_and_model_id_return_200_and_empty_list_when_model_id_is_not_found() throws Exception {

            List<IdValueResponseDto> dto = getYears(getBrandId(), Long.MAX_VALUE);

            assertNotNull(dto);
            assertTrue(dto.isEmpty());
        }

        @Test
        @Order(3)
        public void get_years_by_brand_id_and_model_id_should_return_400_when_model_id_is_not_number() throws Exception {

            Long brandId = getBrandId();
            String modelId = "test";

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, getURL(brandId, modelId));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when model id is wrong[{arguments}]")
        @CsvSource({
                "0",
                "-2938"
        })
        @Order(4)
        public void get_years_by_brand_id_and_model_id_return_400_when_id_is_not_positive(Long modelId) throws Exception {

            Long brandId = getBrandId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, getURL(brandId, modelId));
        }

        @Test
        @Order(5)
        public void get_years_by_brand_id_and_model_id_return_401_token_is_incorrect() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId, modelId));
        }

        @Test
        @Order(6)
        public void get_years_by_brand_id_and_model_id_should_return_401_header_authorization_is_null() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId, modelId));
        }

        @Test
        @Order(7)
        public void get_years_by_brand_id_and_model_id_should_return_401_header_authorization_is_not_bearer() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId, modelId));
        }

        @Test
        @Order(8)
        void get_years_by_brand_id_and_model_id_return_500_when_service_throws_exception() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService).findYears(brandId, modelId);

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR, getURL(brandId, modelId));
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(9)
        void get_years_by_brand_id_and_model_id_return_503_when_database_unavailable() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository).findYears(brandId, modelId);

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_YEARS_URL, brandId, modelId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE, getURL(brandId, modelId));
            } finally {
                reset(carCatalogRepository);
            }
        }
    }

    @Nested
    @DisplayName("GET: /api" + CAR_CATALOG_ENGINES_URL)
    class getEnginesTests {

        private <B, M, Y> String getURL(B brandId, M modelId, Y yearId) {
            return new UriTemplate(CAR_CATALOG_ENGINES_URL).expand(brandId, modelId, yearId).toString();
        }

        @Test
        @Order(1)
        public void get_engines_by_brand_id_model_id_and_year_id_should_return_200() throws Exception {

            List<IdValueResponseDto> dto = getEngines();

            assertNotNull(dto);
            assertFalse(dto.isEmpty());
            dto.forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        public void get_engines_by_brand_id_model_id_and_year_id_return_200_and_empty_list_when_year_id_is_not_found() throws Exception {

            List<IdValueResponseDto> dto = getEngines(getBrandId(), getModelId(), Long.MAX_VALUE);

            assertNotNull(dto);
            assertTrue(dto.isEmpty());
        }

        @Test
        @Order(3)
        public void get_engines_by_brand_id_model_id_and_year_id_should_return_400_when_year_id_is_not_number() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            String yearId = "test";

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, getURL(brandId, modelId, yearId));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when year id is wrong[{arguments}]")
        @CsvSource({
                "0",
                "-2938"
        })
        @Order(4)
        public void get_engines_by_brand_id_model_id_and_year_id_return_400_when_id_is_not_positive(Long yearId) throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, getURL(brandId, modelId, yearId));
        }

        @Test
        @Order(5)
        public void get_engines_by_brand_id_model_id_and_year_id_return_401_token_is_incorrect() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId, modelId, yearId));
        }

        @Test
        @Order(6)
        public void get_engines_by_brand_id_model_id_and_year_id_should_return_401_header_authorization_is_null() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId, modelId, yearId));
        }

        @Test
        @Order(7)
        public void get_engines_by_brand_id_model_id_and_year_id_should_return_401_header_authorization_is_not_bearer() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();

            MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, getURL(brandId, modelId, yearId));
        }

        @Test
        @Order(8)
        void get_engines_by_brand_id_model_id_and_year_id_return_500_when_service_throws_exception() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService).findEngines(brandId, modelId, yearId);

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR, getURL(brandId, modelId, yearId));
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(9)
        void get_engines_by_brand_id_model_id_and_year_id_return_503_when_database_unavailable() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository).findEngines(brandId, modelId, yearId);

                MvcResult result = mockMvc.perform(get(CAR_CATALOG_ENGINES_URL, brandId, modelId, yearId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE, getURL(brandId, modelId, yearId));
            } finally {
                reset(carCatalogRepository);
            }
        }
    }

    @Nested
    @DisplayName("GET: /api" + CAR_CATALOG_TRANSMISSIONS_URL)
    class getTransmissionsTests {

        private <B, M, Y, E> String getURL(B brandId, M modelId, Y yearId, E engineId) {
            return new UriTemplate(CAR_CATALOG_TRANSMISSIONS_URL).expand(brandId, modelId, yearId, engineId).toString();
        }

        @Test
        @Order(1)
        public void get_transmissions_by_brand_id_model_id_year_id_and_engine_id_should_return_200() throws Exception {

            List<IdValueResponseDto> dto = getTransmissions();

            assertNotNull(dto);
            assertFalse(dto.isEmpty());
            dto.forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        public void get_transmissions_by_brand_id_model_id_year_id_and_engine_id_return_200_and_empty_list_when_engine_id_is_not_found() throws Exception {

            List<IdValueResponseDto> dto = getTransmissions(
                    getBrandId(),
                    getModelId(),
                    getYearId(),
                    Long.MAX_VALUE);

            assertNotNull(dto);
            assertTrue(dto.isEmpty());
        }

        @Test
        @Order(3)
        public void get_transmissions_by_brand_id_model_id_year_id_and_engine_id_should_return_400_when_engine_id_is_not_number() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            String engineId = "test";

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_TRANSMISSIONS_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST,
                    getURL(brandId, modelId, yearId, engineId));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when engine id is wrong[{arguments}]")
        @CsvSource({
                "0",
                "-2938"
        })
        @Order(4)
        public void get_transmissions_by_brand_id_model_id_year_id_and_engine_id_should_return_400_when_id_is_not_positive(Long engineId) throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_TRANSMISSIONS_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST,
                    getURL(brandId, modelId, yearId, engineId));
        }

        @Test
        @Order(5)
        public void get_transmissions_by_brand_id_model_id_year_id_and_engine_id_should_return_401_token_is_incorrect() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_TRANSMISSIONS_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED,
                    getURL(brandId, modelId, yearId, engineId));
        }

        @Test
        @Order(6)
        public void get_engines_by_brand_id_model_id_and_year_id_should_return_401_header_authorization_is_null() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_TRANSMISSIONS_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED,
                    getURL(brandId, modelId, yearId, engineId));
        }

        @Test
        @Order(7)
        public void get_engines_by_brand_id_model_id_and_year_id_should_return_401_header_authorization_is_not_bearer() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_TRANSMISSIONS_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED,
                    getURL(brandId, modelId, yearId, engineId));
        }

        @Test
        @Order(8)
        void get_engines_by_brand_id_model_id_and_year_id_should_return_500_when_service_throws_exception() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService).findTransmissions(brandId, modelId, yearId, engineId);

                MvcResult result = mockMvc.perform(get(
                                CAR_CATALOG_TRANSMISSIONS_URL,
                                brandId,
                                modelId,
                                yearId,
                                engineId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR,
                        getURL(brandId, modelId, yearId, engineId));
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(9)
        void get_engines_by_brand_id_model_id_and_year_id_should_return_503_when_database_unavailable() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository).findTransmissions(brandId, modelId, yearId, engineId);

                MvcResult result = mockMvc.perform(get(
                                CAR_CATALOG_TRANSMISSIONS_URL,
                                brandId,
                                modelId,
                                yearId,
                                engineId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE,
                        getURL(brandId, modelId, yearId, engineId));
            } finally {
                reset(carCatalogRepository);
            }
        }
    }

    @Nested
    @DisplayName("GET: /api" + CAR_CATALOG_WHEEL_DRIVES_URL)
    class getWheelDrivesTests {

        private <B, M, Y, E, T> String getURL(B brandId, M modelId, Y yearId, E engineId, T transmissionId) {
            return new UriTemplate(CAR_CATALOG_WHEEL_DRIVES_URL)
                    .expand(brandId, modelId, yearId, engineId, transmissionId).toString();
        }

        @Test
        @Order(1)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_200() throws Exception {

            List<IdValueResponseDto> dto = getWheelDrives();

            assertNotNull(dto);
            assertFalse(dto.isEmpty());
            dto.forEach(Assertions::assertNotNull);
        }

        @Test
        @Order(2)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_200_and_empty_list_when_transmission_id_is_not_found() throws Exception {

            List<IdValueResponseDto> dto = getWheelDrives(
                    getBrandId(),
                    getModelId(),
                    getYearId(),
                    getTransmissionId(),
                    Long.MAX_VALUE);

            assertNotNull(dto);
            assertTrue(dto.isEmpty());
        }

        @Test
        @Order(3)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_400_when_transmission_id_is_not_number() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            String transmissionId = "test";

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_WHEEL_DRIVES_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId,
                            transmissionId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST,
                    getURL(brandId, modelId, yearId, engineId, transmissionId));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when transmission id is wrong[{arguments}]")
        @CsvSource({
                "0",
                "-2938"
        })
        @Order(4)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_400_when_id_is_not_positive(Long transmissionId) throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_WHEEL_DRIVES_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId,
                            transmissionId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST,
                    getURL(brandId, modelId, yearId, engineId, transmissionId));
        }

        @Test
        @Order(5)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_401_token_is_incorrect() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            Long transmissionId = getTransmissionId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_WHEEL_DRIVES_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId,
                            transmissionId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED,
                    getURL(brandId, modelId, yearId, engineId, transmissionId));
        }

        @Test
        @Order(6)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_401_header_authorization_is_null() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            Long transmissionId = getTransmissionId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_WHEEL_DRIVES_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId,
                            transmissionId))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED,
                    getURL(brandId, modelId, yearId, engineId, transmissionId));
        }

        @Test
        @Order(7)
        public void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_401_header_authorization_is_not_bearer() throws Exception {

            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            Long transmissionId = getTransmissionId();

            MvcResult result = mockMvc.perform(get(
                            CAR_CATALOG_WHEEL_DRIVES_URL,
                            brandId,
                            modelId,
                            yearId,
                            engineId,
                            transmissionId)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED,
                    getURL(brandId, modelId, yearId, engineId, transmissionId));
        }

        @Test
        @Order(8)
        void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_500_when_service_throws_exception() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            Long transmissionId = getTransmissionId();
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService)
                        .findWheelDrives(brandId, modelId, yearId, engineId, transmissionId);

                MvcResult result = mockMvc.perform(get(
                                CAR_CATALOG_WHEEL_DRIVES_URL,
                                brandId,
                                modelId,
                                yearId,
                                engineId,
                                transmissionId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR,
                        getURL(brandId, modelId, yearId, engineId, transmissionId));
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(9)
        void get_wheel_drives_by_brand_id_model_id_year_id_engine_id_and_transmission_id_should_return_503_when_database_unavailable() throws Exception {
            Long brandId = getBrandId();
            Long modelId = getModelId();
            Long yearId = getYearId();
            Long engineId = getEngineId();
            Long transmissionId = getTransmissionId();
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository)
                        .findWheelDrives(brandId, modelId, yearId, engineId, transmissionId);

                MvcResult result = mockMvc.perform(get(
                                CAR_CATALOG_WHEEL_DRIVES_URL,
                                brandId,
                                modelId,
                                yearId,
                                engineId,
                                transmissionId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1))
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE,
                        getURL(brandId, modelId, yearId, engineId, transmissionId));
            } finally {
                reset(carCatalogRepository);
            }
        }
    }

    @Nested
    @DisplayName("POST: /api" + CAR_CATALOG_RESOLVE_URL)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class resolveTests {

        private CarResolveRequest getRequestDto() throws Exception {
            return CarResolveRequest.builder()
                    .brandId(getBrandId())
                    .modelId(getModelId())
                    .yearId(getYearId())
                    .engineId(getEngineId())
                    .transmissionId(getTransmissionId())
                    .wheelDriveId(getWheelDriveId())
                    .build();
        }

        private String getDtoJson(CarResolveRequest dto) throws Exception {
            return mapper.writeValueAsString(dto);
        }

        private String getDtoJson() throws Exception {
            return getDtoJson(getRequestDto());
        }

        @Test
        @Order(1)
        public void get_desired_car_id_should_return_200() throws Exception {

            String dtoJson = getDtoJson();

            mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.carId").isNumber())
                    .andExpect(jsonPath("$.carId").isNotEmpty())
                    .andExpect(jsonPath("$.carId", notNullValue()))
                    .andExpect(jsonPath("$.carId", greaterThan(0)));
        }

        @ParameterizedTest(name = "Test {index}: Get desired car id should return 400 when request dto have wrong values")
        @MethodSource("wrongCarResolveRequest")
        @Order(2)
        public void get_desired_car_id_should_return_400_when_request_dto_have_wrong_values(CarResolveRequest dto) throws Exception {

            String dtoJson = getDtoJson(dto);

            MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.BAD_REQUEST, CAR_CATALOG_RESOLVE_URL);
        }


        private Stream<Arguments> wrongCarResolveRequest() throws Exception {
            return Stream.of(
                    Arguments.of(CarResolveRequest.builder()
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(-10L)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(-98L)
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(-9L)
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(-17L)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(-98177L)
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(-7L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(-10L)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(-9765L)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(-45L)
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(-190L)
                            .wheelDriveId(-77L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(-60L)
                            .modelId(getModelId())
                            .yearId(-98177L)
                            .engineId(-9L)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(-87L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(-60L)
                            .modelId(getModelId())
                            .yearId(-98177L)
                            .engineId(-9L)
                            .transmissionId(-7776L)
                            .wheelDriveId(-87L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(-60L)
                            .modelId(-6L)
                            .yearId(-98177L)
                            .engineId(-9L)
                            .transmissionId(-7776L)
                            .wheelDriveId(-87L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(0L)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(0L)
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(0L)
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(0L)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(0L)
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(0L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(0L)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(0L)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(0L)
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(0L)
                            .wheelDriveId(0L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(0L)
                            .modelId(getModelId())
                            .yearId(0L)
                            .engineId(0L)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(0L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(0L)
                            .modelId(getModelId())
                            .yearId(0L)
                            .engineId(0L)
                            .transmissionId(0L)
                            .wheelDriveId(0L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(0L)
                            .modelId(0L)
                            .yearId(0L)
                            .engineId(0L)
                            .transmissionId(0L)
                            .wheelDriveId(0L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .modelId(getModelId())
                            .yearId(0L)
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(-34L)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(-9L)
                            .yearId(0L)
                            .wheelDriveId(-918188L)
                            .build())
            );
        }

        @ParameterizedTest(name = "Test {index}: Get desired car id should return 404 when request dto contains non-existent data")
        @MethodSource("сarResolveRequestWithNonExistentData")
        @Order(3)
        public void get_desired_car_id_should_return_404_when_request_dto_contains_non_existent_data(CarResolveRequest dto) throws Exception {

            String dtoJson = getDtoJson(dto);

            MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.NOT_FOUND, CAR_CATALOG_RESOLVE_URL);
        }

        private Stream<Arguments> сarResolveRequestWithNonExistentData() throws Exception {
            return Stream.of(
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(Long.MAX_VALUE)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(Long.MAX_VALUE)
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(Long.MAX_VALUE)
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(Long.MAX_VALUE)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(Long.MAX_VALUE)
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(Long.MAX_VALUE)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(Long.MAX_VALUE)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(getEngineId())
                            .transmissionId(Long.MAX_VALUE)
                            .wheelDriveId(getWheelDriveId())
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(Long.MAX_VALUE)
                            .yearId(getYearId())
                            .engineId(Long.MAX_VALUE)
                            .transmissionId(getTransmissionId())
                            .wheelDriveId(Long.MAX_VALUE)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(Long.MAX_VALUE)
                            .modelId(getModelId())
                            .yearId(getYearId())
                            .engineId(Long.MAX_VALUE)
                            .transmissionId(Long.MAX_VALUE)
                            .wheelDriveId(Long.MAX_VALUE)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(getBrandId())
                            .modelId(Long.MAX_VALUE)
                            .yearId(Long.MAX_VALUE)
                            .engineId(Long.MAX_VALUE)
                            .transmissionId(Long.MAX_VALUE)
                            .wheelDriveId(Long.MAX_VALUE)
                            .build()),
                    Arguments.of(CarResolveRequest.builder()
                            .brandId(Long.MAX_VALUE)
                            .modelId(Long.MAX_VALUE)
                            .yearId(Long.MAX_VALUE)
                            .engineId(Long.MAX_VALUE)
                            .transmissionId(Long.MAX_VALUE)
                            .wheelDriveId(Long.MAX_VALUE)
                            .build())

            );
        }

        @Test
        @Order(4)
        public void get_desired_car_id_should_return_401_token_is_incorrect() throws Exception {

            String dtoJson = getDtoJson();

            MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Wrong token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson)
                    )
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, CAR_CATALOG_RESOLVE_URL);
        }

        @Test
        @Order(5)
        public void get_desired_car_id_should_return_401_header_authorization_is_null() throws Exception {

            String dtoJson = getDtoJson();

            MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson)
                    )
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, CAR_CATALOG_RESOLVE_URL);
        }

        @Test
        @Order(6)
        public void get_desired_car_id_should_return_401_header_authorization_is_not_bearer() throws Exception {

            String dtoJson = getDtoJson();

            MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Test " + tmpToken1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson)
                    )
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            checkErrorResponseResult(result, HttpStatus.UNAUTHORIZED, CAR_CATALOG_RESOLVE_URL);

        }

        @Test
        @Order(7)
        void get_desired_car_id_should_return_500_when_service_throws_exception() throws Exception {

            String dtoJson = getDtoJson();
            try {
                doThrow(new RuntimeException("Temporary service error."))
                        .when(carCatalogService)
                        .getResolve(any(CarResolveRequest.class));

                MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dtoJson)
                        )
                        .andExpect(status().isInternalServerError())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.INTERNAL_SERVER_ERROR, CAR_CATALOG_RESOLVE_URL);
            } finally {
                reset(carCatalogService);
            }
        }

        @Test
        @Order(8)
        void get_desired_car_id_should_return_503_when_database_unavailable() throws Exception {

            String dtoJson = getDtoJson();
            try {
                doThrow(new RuntimeException("DB down"))
                        .when(carCatalogRepository)
                        .getResolve(
                                any(Long.class),
                                any(Long.class),
                                any(Long.class),
                                any(Long.class),
                                any(Long.class),
                                any(Long.class)
                        );

                MvcResult result = mockMvc.perform(post(CAR_CATALOG_RESOLVE_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tmpToken1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dtoJson)
                        )
                        .andExpect(status().isServiceUnavailable())
                        .andReturn();

                checkErrorResponseResult(result, HttpStatus.SERVICE_UNAVAILABLE, CAR_CATALOG_RESOLVE_URL);
            } finally {
                reset(carCatalogRepository);
            }
        }
    }
}

