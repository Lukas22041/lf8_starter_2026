package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProjectEmployeeRepositoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private ProjectRepository projects;

  @Autowired
  private ProjectEmployeeRepository repository;

  private ProjectEntity saveProject(String description) {
    return projects.save(new ProjectEntity(description, 1L, 4711L, null, null,
        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), null));
  }

  @Test
  void findByProjectReturnsOnlyAssignmentsOfThatProject() {
    // Arrange
    ProjectEntity project = saveProject("Lagerverwaltung");
    ProjectEntity otherProject = saveProject("Webshop");
    repository.save(new ProjectEmployeeEntity(project, 1L, 3L));
    repository.save(new ProjectEmployeeEntity(otherProject, 2L, 4L));

    // Act & Assert
    assertThat(repository.findByProject(project))
        .extracting(ProjectEmployeeEntity::getEmployeeId)
        .containsExactly(1L);
  }
}
