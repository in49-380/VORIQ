package com.voriq.parser_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import util.TestDataLoader;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void shouldCreateCarsFromJson() throws Exception {

        String jsonContent = TestDataLoader.loadJsonFromFileToString("data/test-cars.json");

        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void getAll_shouldReturnCorrectBrands() throws Exception {

        mockMvc.perform(get("/brands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Acura"))
                .andExpect(jsonPath("$[1].name").value("Alpina"));
    }

    @Test
    @Order(3)
    void getById_shouldReturnCorrectBrand() throws Exception {

        mockMvc.perform(get("/brands/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acura"));
    }

    @Test
    @Order(4)
    void getById_shouldReturnCorrectBrandNegative() throws Exception {

        mockMvc.perform(get("/brands/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}