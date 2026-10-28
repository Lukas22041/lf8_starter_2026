package de.szut.lf8_starter.project;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class ProjectGetIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository repository;

    @Test
    void getAllReturns200AndSavedProjects() throws Exception {
        // Arrange
        ProjectEntity saved = repository.save(new ProjectEntity("Lagerverwaltung", 1L, 4711L, "Frau Meier",
                "Altes Lager ablösen", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), null));

        // Act & Assert
        mockMvc.perform(get("/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ProjectID").value(saved.getId()))
                .andExpect(jsonPath("$[0].Description").value("Lagerverwaltung"))
                .andExpect(jsonPath("$[0].ProjectLeadID").value(1))
                .andExpect(jsonPath("$[0].CustomerID").value(4711))
                .andExpect(jsonPath("$[0].Contact").value("Frau Meier"))
                .andExpect(jsonPath("$[0].ProjectGoal").value("Altes Lager ablösen"))
                .andExpect(jsonPath("$[0].StartDate").value("2026-03-01"))
                .andExpect(jsonPath("$[0].PlannedEndDate").value("2026-03-31"))
                .andExpect(jsonPath("$[0].EndDate").isEmpty());
    }

    @Test
    void getAllWithoutProjectsReturnsEmptyList() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized());
    }
}
