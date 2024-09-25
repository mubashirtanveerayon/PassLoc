package helper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


public class NotificationCenter {

    public static void sendSuccessNotification(String message){
//        TrayNotification notification = new TrayNotification("Success",message, NotificationType.SUCCESS);
//        notification.setAnimationType(AnimationType.POPUP);
//        notification.showAndDismiss(Duration.millis(2000));

        if(State.showAllNotifications)

            Notifications.create()
                    .title("Success")
                    .text(message)
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.BOTTOM_RIGHT)
                    .showInformation();



    }

    public static void sendFailureNotification(String message){
//        TrayNotification notification = new TrayNotification("Error",message, NotificationType.ERROR);
//        notification.setAnimationType(AnimationType.POPUP);
//        notification.showAndDismiss(Duration.millis(2000));

         Notifications.create()
                .title("Error")
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .showError();
    }


}
