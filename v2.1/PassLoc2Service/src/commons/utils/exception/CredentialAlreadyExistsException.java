package commons.utils.exception;

public class CredentialAlreadyExistsException extends RuntimeException{

    public CredentialAlreadyExistsException(){
        super("An instance of Credential already exists");
    }

    public CredentialAlreadyExistsException(String message){
        super(message);
    }
}
