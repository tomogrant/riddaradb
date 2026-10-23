package com.se.riddaradb.exceptions;

public class TooManyResultsException extends RuntimeException {
    public TooManyResultsException(String message){
        super(message);
    }
}
