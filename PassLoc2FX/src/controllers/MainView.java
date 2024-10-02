/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.State;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Material;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import services.database.Database;
import services.secure.Credential;

/**
 *
 * @author admin
 */
public class MainView extends View implements Initializable {
    
    double mouseX = 0, mouseY = 0;
    
    @FXML
    private AnchorPane topBar;
    
    @FXML
    BorderPane containerPane;


    TutorialView tutorialPopOverView;

    @FXML
    ImageView helpIcon;

    @FXML
    private Button lockButton;

    @FXML
    private JFXToggleButton notificationToggleButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
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
        
        FXMLLoader unlockViewLoader = new FXMLLoader(MainView.class.getResource("/res/view/unlock-view.fxml"));
        try {
            Parent view = unlockViewLoader.load();
            containerPane.setCenter(view);
            
            UnlockView controller = unlockViewLoader.getController();
            controller.setBorderPane(containerPane);
            controller.setLockButton(lockButton);

        } catch (IOException ex) {
           ex.printStackTrace();
        }



        FXMLLoader tutorialLoader = new FXMLLoader(getClass().getResource("/res/view/tutorial-view.fxml"));;
        try{
            Parent root = tutorialLoader.load();
            tutorialPopOverView = tutorialLoader.getController()    ;


        }catch(IOException ex){
            ex.printStackTrace();
        }







    }
    
    public void onAccessDBMouseClicked(MouseEvent mouseEvent) {
        if(State.currentState == State.AppState.MASTER_LOGIN || State.currentState == State.AppState.DB_LOGIN)return;

        FXMLLoader loginViewLoader = new FXMLLoader(getClass().getResource("/res/view/db-login-view.fxml"));
        try{
            containerPane.setCenter(loginViewLoader.load());
            LoginDBView controller = loginViewLoader.getController();
            controller.setBorderPane(containerPane);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    public void onViewMouseClicked(MouseEvent mouseEvent) {
        if(State.currentState == State.AppState.MASTER_LOGIN || State.currentState == State.AppState.VIEW)return;
        FXMLLoader dataViewLoader = new FXMLLoader(getClass().getResource("/res/view/data-view.fxml"));
        try{
            containerPane.setCenter(dataViewLoader.load());
            DataView controller = dataViewLoader.getController();
            controller.setBorderPane(containerPane);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void onPGMouseClicked(MouseEvent mouseEvent) {
        if(State.currentState == State.AppState.MASTER_LOGIN || State.currentState == State.AppState.PG)return;
        FXMLLoader pgViewLoader = new FXMLLoader(getClass().getResource("/res/view/pg-view.fxml"));
        try{
            containerPane.setCenter(pgViewLoader.load());
            PasswordGeneratorView controller = pgViewLoader.getController();
            controller.setBorderPane(containerPane);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void onSyncMouseClicked(MouseEvent mouseEvent) {
        if(State.currentState == State.AppState.MASTER_LOGIN || State.currentState == State.AppState.SYNC)return;
        FXMLLoader syncViewLoader = new FXMLLoader(getClass().getResource("/res/view/sync-view.fxml"));
        try{
            containerPane.setCenter(syncViewLoader.load());
            SyncView controller = syncViewLoader.getController();
            controller.setBorderPane(containerPane);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void onLogoutMouseClicked(MouseEvent mouseEvent) {
        if(State.currentState == State.AppState.MASTER_LOGIN || State.currentState == State.AppState.DB_LOGIN)return;
        if(Database.online()) {
            Database.disconnect();

            Credential.getInstance().resetEncryptor();
        }
        FXMLLoader loginViewLoader = new FXMLLoader(getClass().getResource("/res/view/db-login-view.fxml"));
        try{
            containerPane.setCenter(loginViewLoader.load());
            LoginDBView controller = loginViewLoader.getController();
            controller.setBorderPane(containerPane);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
    
    public void onCloseButtonClicked(MouseEvent me) {
        if(Database.online())
            Database.disconnect();
        Credential.resetInstance();
        System.exit(0);
    }

    public void onMinimizeButtonClicked(MouseEvent me) {
        Stage mainStage = (Stage) (((Node) me.getSource()).getScene().getWindow());
        mainStage.setIconified(true);
    }

    @FXML
    void onLockButtonAction(ActionEvent event) {


        if(Credential.getInstance() == null)
            return;
        if(Database.online())
            Database.disconnect();

        Credential.resetInstance();
        FXMLLoader unlockViewLoader = new FXMLLoader(getClass().getResource("/res/view/unlock-view.fxml"));
        try{

            containerPane.setCenter(unlockViewLoader.load());
            UnlockView controller = (UnlockView)unlockViewLoader.getController();
            controller.setLockButton(lockButton);
            controller.setBorderPane(containerPane);
            lockButton.setDisable(true);

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void onHelpButtonClicked(MouseEvent me) {
        if(tutorialPopOverView.isShowing())
            tutorialPopOverView.hide();
        else
            tutorialPopOverView.show(helpIcon);
    }

    @FXML
    void onNotificationToggle(ActionEvent event) {
        State.showAllNotifications = notificationToggleButton.isSelected();
    }
}
