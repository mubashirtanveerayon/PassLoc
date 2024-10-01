package com.loc.service.utils.exception;

public class CredentialAuthenticationFailedException extends RuntimeException{
    public CredentialAuthenticationFailedException(){
        super("Credentials did not match");
    }
}
