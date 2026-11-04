package de.szut.lf8_starter.project;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployeeEntity, Long> {

    List<ProjectEmployeeEntity> findByProject(ProjectEntity project);

    Optional<ProjectEmployeeEntity> findByProjectAndEmployeeId(ProjectEntity project, long employeeId);

    boolean existsByProjectAndEmployeeId(ProjectEntity project, Long employeeId);

    /** Zwei Zeiträume überschneiden sich, wenn jeder spätestens am Endtag des anderen beginnt (beide Tage inklusive). */
    @Query("""
            select count(pe) > 0 from ProjectEmployeeEntity pe
            where pe.employeeId = :employeeId
              and pe.project.StartDate <= :plannedEndDate
              and pe.project.PlannedEndDate >= :startDate
            """)
    boolean isEmployeePlannedBetween(Long employeeId, LocalDate startDate, LocalDate plannedEndDate);
}
