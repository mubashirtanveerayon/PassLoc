package helper;

import javafx.util.Duration;
import utils.dbInterface.DatabaseListener;


public class DatabaseEventListener implements DatabaseListener {
    @Override
    public void onSuccess(String s) {
        NotificationCenter.sendSuccessNotification(s);
    }

    @Override
    public void onFailure(String s) {
        NotificationCenter.sendFailureNotification(s);
    }

    @Override
    public void onUpgrade(String s) {

    }
}
