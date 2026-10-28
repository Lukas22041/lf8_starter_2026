package de.szut.lf8_starter.project;

import static org.hamcrest.Matchers.contains;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class ProjectEmployeesGetIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projects;

    @Autowired
    private ProjectEmployeeRepository projectEmployees;

    private ProjectEntity saveProject() {
        return projects.save(new ProjectEntity("Lagerverwaltung", 1L, 4711L, "Frau Meier",
                "Altes Lager ablösen", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), null));
    }

    @Test
    void getEmployeesReturns200AndAssignedEmployees() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        projectEmployees.save(new ProjectEmployeeEntity(project, 1L, 3L));
        projectEmployees.save(new ProjectEmployeeEntity(project, 2L, 4L));

        // Act & Assert
        mockMvc.perform(get("/projects/{id}/employees", project.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(project.getId()))
                .andExpect(jsonPath("$.description").value("Lagerverwaltung"))
                .andExpect(jsonPath("$.employees.length()").value(2))
                .andExpect(jsonPath("$.employees[?(@.employeeId == 1)].qualificationId").value(contains(3)))
                .andExpect(jsonPath("$.employees[?(@.employeeId == 2)].qualificationId").value(contains(4)));
    }

    @Test
    void getEmployeesWithoutAssignmentsReturnsEmptyList() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();

        // Act & Assert
        mockMvc.perform(get("/projects/{id}/employees", project.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees.length()").value(0));
    }

    @Test
    void getEmployeesOfUnknownProjectReturns404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/projects/999999/employees").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Projekt 999999 nicht gefunden"));
    }

    @Test
    void getEmployeesWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/projects/1/employees"))
                .andExpect(status().isUnauthorized());
    }
}
