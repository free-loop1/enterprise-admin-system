package com.freeloop.student.v4;

public class StudentPersistenceException extends RuntimeException {
    public StudentPersistenceException(String message) {
        super(message);
    }

    public StudentPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
