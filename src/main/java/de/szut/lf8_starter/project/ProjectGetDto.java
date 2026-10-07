package de.szut.lf8_starter.project;

import java.util.Date;

/** DTO for get requests */
public record ProjectGetDto(
        Long ProjectID,
        String Description,
        Long ProjectLeadID,
        Long CustomerID,
        String Contact,
        String ProjectGoal,
        Date StartDate,
        Date PlannedEndDate,
        Date EndDate
) {

}
