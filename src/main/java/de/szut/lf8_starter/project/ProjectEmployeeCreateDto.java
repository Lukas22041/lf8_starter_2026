package de.szut.lf8_starter.project;

import jakarta.validation.constraints.NotNull;

public record ProjectEmployeeCreateDto(
    @NotNull(message = "wert muss vorhanden sein")
    Long employeeId,
    @NotNull(message = "wert muss vorhanden sein")
    Long qualificationId) {
}