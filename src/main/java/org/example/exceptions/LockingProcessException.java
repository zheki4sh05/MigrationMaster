package org.example.exceptions;

public class LockingProcessException extends RuntimeException{
    public LockingProcessException(String message) {
        super(message);
    }
}
