package com.example.exception;

public class SQLiteInternalError extends RuntimeException{
    public SQLiteInternalError(String message){
        super(message);
    }
}
