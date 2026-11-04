package de.szut.lf8_starter.project;

public class ProjectEmployeeNotFoundException extends RuntimeException {

  public ProjectEmployeeNotFoundException(long projectId, long employeeId) {
    super("Mitarbeiter " + employeeId + " ist Projekt " + projectId + " nicht zugeordnet");
  }
}