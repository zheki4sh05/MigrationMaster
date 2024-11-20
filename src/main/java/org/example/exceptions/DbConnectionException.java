package org.example.exceptions;

public class DbConnectionException extends RuntimeException{
    public DbConnectionException(String message) {
        super(message);
    }
}
