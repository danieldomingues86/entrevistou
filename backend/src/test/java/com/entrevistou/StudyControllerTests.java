package com.entrevistou;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTests {
    @Autowired MockMvc mvc;

    @Test
    void dashboardLoads() throws Exception {
        mvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readiness").isNumber())
                .andExpect(jsonPath("$.topics").isArray());
    }

    @Test
    void learningPathHasSixModules() throws Exception {
        mvc.perform(get("/api/learning-path"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules.length()").value(6));
    }
}
