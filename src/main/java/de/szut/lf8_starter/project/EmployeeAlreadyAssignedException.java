package de.szut.lf8_starter.project;

public class EmployeeAlreadyAssignedException extends RuntimeException {

    public EmployeeAlreadyAssignedException(long employeeId, long projectId) {
        super("Mitarbeiter " + employeeId + " ist Projekt " + projectId + " bereits zugeordnet");
    }
}
