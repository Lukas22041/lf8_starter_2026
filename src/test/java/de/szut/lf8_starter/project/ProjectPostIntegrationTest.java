package de.szut.lf8_starter.project;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ProjectPostIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String validRequest() {
        return """
                {
                  "Description": "Neues Projekt",
                  "ProjectLeadID": 1,
                  "CustomerID": 2,
                  "Contact": "Erika Muster",
                  "ProjectGoal": "Migration",
                  "StartDate": "2026-03-01",
                  "PlannedEndDate": "2026-03-31"
                }
                """;
    }

    @Test
    void createReturns201() throws Exception {
        when(employeeClient.findById(1L)).thenReturn(Optional.of(new EmployeeDto(1L, "Max", "Mustermann", List.of())));

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.ProjectID").isNumber())
                .andExpect(jsonPath("$.Description").value("Neues Projekt"))
                .andExpect(jsonPath("$.ProjectLeadID").value(1))
                .andExpect(jsonPath("$.StartDate").value("2026-03-01"))
                .andExpect(jsonPath("$.PlannedEndDate").value("2026-03-31"));
    }

    @Test
    void createWithoutDescriptionReturns400() throws Exception {
        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ProjectLeadID": 1,
                                  "CustomerID": 2,
                                  "StartDate": "2026-03-01",
                                  "PlannedEndDate": "2026-03-31"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.Description").exists());
    }

    @Test
    void createWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithPlannedEndBeforeStartReturns422() throws Exception {
        when(employeeClient.findById(1L)).thenReturn(Optional.of(new EmployeeDto(1L, "Max", "Mustermann", List.of())));

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "Description": "Neues Projekt",
                                  "ProjectLeadID": 1,
                                  "CustomerID": 2,
                                  "StartDate": "2026-03-31",
                                  "PlannedEndDate": "2026-03-01"
                                }
                                """))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void createWithUnknownProjectLeadReturns404() throws Exception {
        when(employeeClient.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isNotFound());
    }
}
