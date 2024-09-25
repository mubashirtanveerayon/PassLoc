package helper;

import javafx.util.Duration;


public class DatabaseListener implements utils.dbInterface.DatabaseListener {
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
