package services.commons.utils.exception;

public class MissingChunkException extends RuntimeException{

    public MissingChunkException(int chunkIndex){
        super("Missing chunk "+chunkIndex+".");
    }

}
