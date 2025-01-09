package commons.utils.dbInterface;

public interface DatabaseListener {

    public void onSuccess(String message);
    public void onFailure(String message);
    public void onUpgrade(String message);
}
