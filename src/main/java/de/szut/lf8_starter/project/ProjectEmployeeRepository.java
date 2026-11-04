package de.szut.lf8_starter.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployeeEntity, Long> {

    List<ProjectEmployeeEntity> findByProject(ProjectEntity project);
}
