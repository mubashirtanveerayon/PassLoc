package commons.utils.exception;

public class SQLComAlreadyExistsException extends RuntimeException{

    public SQLComAlreadyExistsException(){
        super("An instance already exists.");
    }

    public SQLComAlreadyExistsException(String message){
        super(message);
    }
}
