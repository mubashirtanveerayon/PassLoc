package utils.exception;

public class CredentialAuthenticationFailedException extends RuntimeException{
    public CredentialAuthenticationFailedException(){
        super("Credentials did not match");
    }
}