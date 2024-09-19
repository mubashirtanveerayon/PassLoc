package utils.exception;

public class InvalidChunkException extends RuntimeException{
    public InvalidChunkException(int chunkIndex){

        super("Invalid chunk: "+chunkIndex+", chunk is null or empty.");

    }

    public InvalidChunkException(int chunkIndex,String chunk){
        super("Invalid chunk: "+chunkIndex+", data: "+chunk);
    }
}
