package commons.exceptions;

public class MissingChunkException extends RuntimeException{

    public MissingChunkException(int chunkIndex){
        super("Missing chunk "+chunkIndex+".");
    }

}
