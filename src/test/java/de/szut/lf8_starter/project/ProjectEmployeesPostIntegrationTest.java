package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.ConnectException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import de.szut.lf8_starter.employee.QualificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

class ProjectEmployeesPostIntegrationTest extends AbstractIntegrationTest {

    private static final long EMPLOYEE_ID = 1L;
    private static final long JAVA = 3L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projects;

    @Autowired
    private ProjectEmployeeRepository projectEmployees;

    @BeforeEach
    void employeeKnowsJava() {
        when(employeeClient.findById(EMPLOYEE_ID)).thenReturn(Optional.of(
                new EmployeeDto(EMPLOYEE_ID, "Max", "Mustermann", List.of(new QualificationDto(JAVA, "Java")))));
    }

    private ProjectEntity saveProject(LocalDate start, LocalDate plannedEnd) {
        return projects.save(new ProjectEntity("Lagerverwaltung", 1L, 4711L, null, null, start, plannedEnd, null));
    }

    private ProjectEntity saveMarchProject() {
        return saveProject(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));
    }

    private ResultActions assign(ProjectEntity project, long employeeId, long qualificationId) throws Exception {
        return mockMvc.perform(post("/projects/{id}/employees", project.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"employeeId": %d, "qualificationId": %d}
                        """.formatted(employeeId, qualificationId)));
    }

    @Test
    void assignReturns201() throws Exception {
        // Arrange
        ProjectEntity project = saveMarchProject();

        // Act & Assert
        assign(project, EMPLOYEE_ID, JAVA)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.employeeId").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$.qualificationId").value(JAVA));
        assertThat(projectEmployees.findByProject(project)).hasSize(1);
    }

    @Test
    void assignWithoutQualificationIdReturns400() throws Exception {
        // Arrange
        ProjectEntity project = saveMarchProject();

        // Act & Assert
        mockMvc.perform(post("/projects/{id}/employees", project.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.qualificationId").exists());
    }

    @Test
    void assignWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/projects/1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId": 1, "qualificationId": 3}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void assignToUnknownProjectReturns404() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/projects/999999/employees")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId": 1, "qualificationId": 3}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Projekt 999999 nicht gefunden"));
    }

    @Test
    void assignUnknownEmployeeReturns404() throws Exception {
        // Arrange
        ProjectEntity project = saveMarchProject();
        when(employeeClient.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assign(project, 999L, JAVA)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Mitarbeiter 999 nicht gefunden"));
    }

    @Test
    void assignWithMissingQualificationReturns422() throws Exception {
        // Arrange
        ProjectEntity project = saveMarchProject();

        // Act & Assert
        assign(project, EMPLOYEE_ID, 99L)
                .andExpect(status().isUnprocessableContent());
        assertThat(projectEmployees.findByProject(project)).isEmpty();
    }

    @Test
    void assignSameEmployeeTwiceReturns409() throws Exception {
        // Arrange
        ProjectEntity project = saveMarchProject();
        projectEmployees.save(new ProjectEmployeeEntity(project, EMPLOYEE_ID, JAVA));

        // Act & Assert
        assign(project, EMPLOYEE_ID, JAVA)
                .andExpect(status().isConflict());
    }

    @Test
    void assignWhenPeriodsShareOneDayReturns409() throws Exception {
        // Arrange: A 01.03.–31.03., B 31.03.–15.04.
        projectEmployees.save(new ProjectEmployeeEntity(saveMarchProject(), EMPLOYEE_ID, JAVA));
        ProjectEntity project = saveProject(LocalDate.of(2026, 3, 31), LocalDate.of(2026, 4, 15));

        // Act & Assert
        assign(project, EMPLOYEE_ID, JAVA)
                .andExpect(status().isConflict());
        assertThat(projectEmployees.findByProject(project)).isEmpty();
    }

    @Test
    void assignWhenPeriodStartsDayAfterOtherEndsReturns201() throws Exception {
        // Arrange: A 01.03.–31.03., B 01.04.–30.04.
        projectEmployees.save(new ProjectEmployeeEntity(saveMarchProject(), EMPLOYEE_ID, JAVA));
        ProjectEntity project = saveProject(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        // Act & Assert
        assign(project, EMPLOYEE_ID, JAVA)
                .andExpect(status().isCreated());
    }

    @Test
    void assignAfterOtherProjectWasDeletedReturns201() throws Exception {
        // Arrange
        ProjectEntity deleted = saveMarchProject();
        projectEmployees.save(new ProjectEmployeeEntity(deleted, EMPLOYEE_ID, JAVA));
        mockMvc.perform(delete("/projects/{id}", deleted.getId()).with(jwt()));
        projects.flush();
        ProjectEntity project = saveMarchProject();

        // Act & Assert
        assign(project, EMPLOYEE_ID, JAVA)
                .andExpect(status().isCreated());
    }

    @Test
    void assignWhenEmployeeServiceIsUnavailableReturns503AndSavesNothing() throws Exception {
        // Arrange
        ProjectEntity project = saveMarchProject();
        when(employeeClient.findById(EMPLOYEE_ID))
                .thenThrow(new EmployeeServiceUnavailableException(new ConnectException()));

        // Act & Assert
        assign(project, EMPLOYEE_ID, JAVA)
                .andExpect(status().isServiceUnavailable());
        assertThat(projectEmployees.findByProject(project)).isEmpty();
    }
}
