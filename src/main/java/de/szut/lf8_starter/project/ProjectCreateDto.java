package de.szut.lf8_starter.project;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** DTO for create requests */
public record ProjectCreateDto(
        @NotBlank(message = "wert muss vorhanden sein")
        String Description,
        @NotNull(message = "wert muss vorhanden sein")
        Long ProjectLeadID,
        @NotNull(message = "wert muss vorhanden sein")
        Long CustomerID,
        String Contact,
        String ProjectGoal,
        @NotNull(message = "wert muss vorhanden sein")
        LocalDate StartDate,
        @NotNull(message = "wert muss vorhanden sein")
        LocalDate PlannedEndDate,
        LocalDate EndDate
) {

}
