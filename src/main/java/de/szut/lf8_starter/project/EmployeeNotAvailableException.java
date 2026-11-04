package de.szut.lf8_starter.project;

public class EmployeeNotAvailableException extends RuntimeException {

    public EmployeeNotAvailableException(long employeeId) {
        super("Mitarbeiter " + employeeId + " ist im Projektzeitraum bereits in einem anderen Projekt eingeplant");
    }
}
