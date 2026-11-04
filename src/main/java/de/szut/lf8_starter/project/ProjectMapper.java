package de.szut.lf8_starter.project;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectEntity fromCreate(ProjectCreateDto dto) {
        return new ProjectEntity(dto.Description(), dto.ProjectLeadID(), dto.CustomerID(), dto.Contact(),
                dto.ProjectGoal(), dto.StartDate(), dto.PlannedEndDate(), dto.EndDate());
    }

    public ProjectGetDto toDto(ProjectEntity entity) {
        return new ProjectGetDto(entity.getId(), entity.getDescription(), entity.getProjectLeadID(),
                entity.getCustomerID(), entity.getContact(), entity.getProjectGoal(), entity.getStartDate(),
                entity.getPlannedEndDate(), entity.getEndDate());
    }

    public ProjectWithEmployeesGetDto toWithEmployeesDto(ProjectEntity project, List<ProjectEmployeeEntity> employees) {
        List<ProjectEmployeeGetDto> employeeDtos = employees.stream().map(this::toDto).toList();
        return new ProjectWithEmployeesGetDto(project.getId(), project.getDescription(), employeeDtos);
    }

    public ProjectEmployeeGetDto toDto(ProjectEmployeeEntity entity) {
        return new ProjectEmployeeGetDto(entity.getEmployeeId(), entity.getQualificationId());
    }
}
