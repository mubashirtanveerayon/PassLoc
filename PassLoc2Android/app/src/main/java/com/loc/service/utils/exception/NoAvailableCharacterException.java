package com.loc.service.utils.exception;

public class NoAvailableCharacterException extends RuntimeException{
    public NoAvailableCharacterException(){
        super("No available characters remaining");
    }

}