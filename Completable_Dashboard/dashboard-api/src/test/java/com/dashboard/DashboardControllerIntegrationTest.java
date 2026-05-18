package com.dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.cache.type=simple",          // use in-memory cache in tests
        "spring.data.redis.host=localhost",   // won't actually connect in tests
        "dashboard.widget.timeout-seconds=5"
})
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDashboard_validUserId_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/user-001")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.charts").exists())
                .andExpect(jsonPath("$.profile").exists())
                .andExpect(jsonPath("$.alerts").exists())
                .andExpect(jsonPath("$.revenue").exists())
                .andExpect(jsonPath("$.stock").exists())
                .andExpect(jsonPath("$.tasks").exists())
                .andExpect(jsonPath("$.loadTimeMs").isNumber())
                .andExpect(jsonPath("$.widgetStatuses").exists());
    }

    @Test
    void getHealthCheck_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void getFinancialSummary_validUserId_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/user-001/financial-summary")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.totalStockItems").exists());
    }
}
