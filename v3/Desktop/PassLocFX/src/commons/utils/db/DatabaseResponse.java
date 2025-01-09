package commons.utils.db;

public class DatabaseResponse {


    public final DatabaseAction action;
    public final String response;
    public final boolean success;
    public DatabaseResponse(DatabaseAction action, String response, boolean success) {
        this.action = action;
        this.response = response;
        this.success = success;
    }
}
