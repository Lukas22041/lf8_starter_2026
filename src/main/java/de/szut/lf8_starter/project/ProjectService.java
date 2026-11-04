package de.szut.lf8_starter.project;

import java.util.List;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectEmployeeRepository projectEmployees;
    private final EmployeeClient employees;

    public ProjectEntity create(ProjectEntity entity) {
        if (entity.getPlannedEndDate().isBefore(entity.getStartDate())) {
            throw new InvalidProjectDatesException();
        }
        long leadId = entity.getProjectLeadID();
        employees.findById(leadId)
                .orElseThrow(() -> new EmployeeNotFoundException(leadId));
        return repository.save(entity);
    }

    public List<ProjectEntity> findAll() {
        return repository.findAll();
    }

    public ProjectEntity findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public List<ProjectEmployeeEntity> findEmployees(ProjectEntity project) {
        return projectEmployees.findByProject(project);
    }

    public ProjectEmployeeEntity addEmployee(ProjectEntity project, long employeeId, long qualificationId) {
        EmployeeDto employee = employees.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        if (projectEmployees.existsByProjectAndEmployeeId(project, employeeId)) {
            throw new EmployeeAlreadyAssignedException(employeeId, project.getId());
        }
        boolean qualified = employee.skillSet().stream()
                .anyMatch(qualification -> qualification.id() == qualificationId);
        if (!qualified) {
            throw new MissingQualificationException(employeeId, qualificationId);
        }
        if (projectEmployees.isEmployeePlannedBetween(employeeId, project.getStartDate(), project.getPlannedEndDate())) {
            throw new EmployeeNotAvailableException(employeeId);
        }
        return projectEmployees.save(new ProjectEmployeeEntity(project, employeeId, qualificationId));
    }

    public void delete(long id) {
        ProjectEntity entity = findById(id);
        repository.delete(entity);
    }

    public ProjectEmployeeEntity findEmployee(long projectId, long employeeId) {
        ProjectEntity project = findById(projectId);          // 404: Projekt gibt es nicht

        employees.findById(employeeId)                       // 404: Mitarbeiter gibt es nicht
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId)); // 503: Dienst weg

        return projectEmployees.findByProjectAndEmployeeId(project, employeeId)
            .orElseThrow(() -> new ProjectEmployeeNotFoundException(projectId, employeeId)); // 404
    }
}
