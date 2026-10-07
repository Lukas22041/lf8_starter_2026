package de.szut.lf8_starter.project;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;

/** DTO for create requests */
public record ProjectCreateDto(
        @NotBlank(message = "darf nicht leer sein")
        String Description,
        @NotBlank(message = "darf nicht leer sein")
        Long ProjectLeadID,
        @NotBlank(message = "darf nicht leer sein")
        Long CustomerID,
        String Contact,
        String ProjectGoal,
        @NotBlank(message = "darf nicht leer sein")
        Date StartDate,
        @NotBlank(message = "darf nicht leer sein")
        Date PlannedEndDate,
        Date EndDate
) {

}
