package de.szut.lf8_starter.project;

import java.util.List;

public record ProjectWithEmployeesGetDto(Long projectId, String description, List<ProjectEmployeeGetDto> employees) {
}