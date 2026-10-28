package de.szut.lf8_starter.project;

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
}
