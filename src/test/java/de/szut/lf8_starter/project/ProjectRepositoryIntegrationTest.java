package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProjectRepositoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private ProjectRepository repository;

  @Test
  void saveGeneratesId() {
    // Arrange
    ProjectEntity project = new ProjectEntity("Lagerverwaltung", 1L, 4711L, "Frau Meier",
        "Altes Lager ablösen", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), null);

    // Act
    ProjectEntity saved = repository.save(project);

    // Assert
    assertThat(saved.getId()).isNotNull();
    assertThat(repository.findById(saved.getId())).isPresent();
  }
}