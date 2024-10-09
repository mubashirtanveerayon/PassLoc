package controllers;

import com.jfoenix.controls.JFXToggleButton;
import commons.services.sqlcomm.CommandGenerator;
import helper.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import services.database.Database;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {
    double mouseX = 0, mouseY = 0;

    @FXML
    private AnchorPane topBar;

    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXToggleButton notificationToggleButton;

    public  void setView(String pathToResource){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToResource));
        try{
            borderPane.setCenter(loader.load());
            View controller = (View) loader.getController();
            controller.setBorderPane(borderPane);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            }
        });

        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Stage mainStage = (Stage) (((Node) event.getSource()).getScene().getWindow());
                mainStage.setX(event.getScreenX() - mouseX);
                mainStage.setY(event.getScreenY() - mouseY);

            }
        });


        setView("/res/view/login_view.fxml");

    }
    @FXML
    void onCloseClicked(MouseEvent event) {

        CommandGenerator.disconnect();
        Database.disconnect();
        System.exit(0);

    }

    @FXML
    void onInfoClicked(MouseEvent event) {

    }

    @FXML
    void onLoginClicked(MouseEvent event) {
        setView("/res/view/login_view.fxml");

    }

    @FXML
    void onLogoutClicked(MouseEvent event) {

        CommandGenerator.disconnect();
        Database.disconnect();
        setView("/res/view/login_view.fxml");

    }

    @FXML
    void onMinimizeClicked(MouseEvent event) {
        Stage mainStage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        mainStage.setIconified(true);

    }

    @FXML
    void onPGClicked(MouseEvent event) {

        setView("/res/view/pg_view.fxml");
    }

    @FXML
    void onSyncClicked(MouseEvent event) {
        setView("/res/view/sync_view.fxml");
    }

    @FXML
    void onViewClicked(MouseEvent event) {
        setView("/res/view/data_view.fxml");
    }

    @FXML
    void onNotificationToggle(ActionEvent event) {
        State.showAllNotifications = notificationToggleButton.isSelected();
    }

}
