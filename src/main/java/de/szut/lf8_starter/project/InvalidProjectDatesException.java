package de.szut.lf8_starter.project;

public class InvalidProjectDatesException extends RuntimeException {

    public InvalidProjectDatesException() {
        super("Das geplante Enddatum darf nicht vor dem Startdatum liegen");
    }
}
