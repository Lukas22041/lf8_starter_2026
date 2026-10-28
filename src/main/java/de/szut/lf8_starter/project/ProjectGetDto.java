package de.szut.lf8_starter.project;

import java.time.LocalDate;

/** DTO for get requests */
public record ProjectGetDto(
        Long ProjectID,
        String Description,
        Long ProjectLeadID,
        Long CustomerID,
        String Contact,
        String ProjectGoal,
        LocalDate StartDate,
        LocalDate PlannedEndDate,
        LocalDate EndDate
) {

}
