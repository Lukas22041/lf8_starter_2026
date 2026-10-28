package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
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
}
