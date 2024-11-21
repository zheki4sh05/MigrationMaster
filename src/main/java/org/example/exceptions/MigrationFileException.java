package org.example.exceptions;

public class MigrationFileException extends RuntimeException{
    public MigrationFileException(String message) {
        super(message);
    }
}
