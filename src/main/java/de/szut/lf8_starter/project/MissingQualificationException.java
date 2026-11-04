package de.szut.lf8_starter.project;

public class MissingQualificationException extends RuntimeException {

    public MissingQualificationException(long employeeId, long qualificationId) {
        super("Mitarbeiter " + employeeId + " besitzt die Qualifikation " + qualificationId + " nicht");
    }
}
