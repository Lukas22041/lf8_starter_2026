package de.szut.lf8_starter.project;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String Description;
  private Long ProjectLeadID;
  private Long CustomerID;
  private String Contact;
  private String ProjectGoal;
  private LocalDate StartDate;
  private LocalDate PlannedEndDate;
  private LocalDate EndDate;

  public ProjectEntity(final String description, final Long projectLeadID, final Long customerID, final String contact,
      final String projectGoal, final LocalDate startDate, final LocalDate plannedEndDate, final LocalDate endDate) {
    this.Description = description;
    this.ProjectLeadID = projectLeadID;
    this.CustomerID = customerID;
    this.Contact = contact;
    this.ProjectGoal = projectGoal;
    this.StartDate = startDate;
    this.PlannedEndDate = plannedEndDate;
    this.EndDate = endDate;
  }
}
