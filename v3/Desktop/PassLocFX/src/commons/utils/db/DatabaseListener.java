package commons.utils.db;

public interface DatabaseListener {

    public void onSuccess(DatabaseResponse response);
    public void onFailure(DatabaseResponse response);
    public void onInitialized();
    void onDisconnected();
}
