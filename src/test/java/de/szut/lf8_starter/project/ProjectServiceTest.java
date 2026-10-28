package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService service;

    @Mock
    private ProjectRepository repository;

    @Mock
    private EmployeeClient employees;

    private ProjectEntity createProjectEntity(LocalDate start, LocalDate plannedEnd) {
        return new ProjectEntity("Beschreibung", 1L, 2L, "Kontakt", "Ziel", start, plannedEnd, null);
    }

    @Test
    void createSavesProjectWhenLeadExists() {
        ProjectEntity entity = createProjectEntity(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));
        when(employees.findById(1L)).thenReturn(Optional.of(new EmployeeDto(1L, "Max", "Mustermann", List.of())));
        when(repository.save(entity)).thenReturn(entity);

        assertThat(service.create(entity)).isSameAs(entity);
        verify(repository).save(entity);
    }

    @Test
    void createAllowsPlannedEndOnStartDate() {
        ProjectEntity entity = createProjectEntity(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 1));
        when(employees.findById(1L)).thenReturn(Optional.of(new EmployeeDto(1L, "Max", "Mustermann", List.of())));
        when(repository.save(entity)).thenReturn(entity);

        assertThat(service.create(entity)).isSameAs(entity);
    }

    @Test
    void createThrowsWhenLeadDoesNotExist() {
        ProjectEntity entity = createProjectEntity(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));
        when(employees.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(entity)).isInstanceOf(EmployeeNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenPlannedEndIsBeforeStart() {
        ProjectEntity entity = createProjectEntity(LocalDate.of(2026, 3, 31), LocalDate.of(2026, 3, 1));

        assertThatThrownBy(() -> service.create(entity)).isInstanceOf(InvalidProjectDatesException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void findAllReturnsAllProjects() {
        ProjectEntity entity = createProjectEntity(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));
        when(repository.findAll()).thenReturn(List.of(entity));

        assertThat(service.findAll()).containsExactly(entity);
    }

    @Test
    void findByIdReturnsProject() {
        ProjectEntity entity = createProjectEntity(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(service.findById(1L)).isSameAs(entity);
    }

    @Test
    void findByIdThrowsWhenProjectDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(ProjectNotFoundException.class);
    }
}
