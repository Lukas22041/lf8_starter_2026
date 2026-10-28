package de.szut.lf8_starter.project;

import java.util.List;

import de.szut.lf8_starter.employee.EmployeeClient;
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
}
