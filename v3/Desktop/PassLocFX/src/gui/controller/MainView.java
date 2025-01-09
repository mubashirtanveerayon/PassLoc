package gui.controller;

import commons.utils.db.DatabaseAction;
import commons.utils.db.DatabaseListener;
import commons.utils.db.DatabaseResponse;
import database.Database;
import gui.Main;
import gui.features.CenterView;
import gui.features.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainView extends View implements DatabaseListener {

    public CenterView currentCenterView;
    private CenterView previousCenterView;

    double mouseX = 0, mouseY = 0;

    @FXML
    private Button lockButton;

    @FXML
    private Button newButton;


    @FXML
    private Button showButton;

    @FXML
    private Button syncButton;

    @FXML
    private Label viewLabel;

    public MainView(){
        Database.addListener(this);
    }

    @FXML
    void onCloseClicked(MouseEvent event) {

        Database.disconnect();
        System.exit(0);

    }

    @FXML
    void onEntriesPressed(ActionEvent event) {
        setCenterView((CenterView)loadView("/gui/res/view/show-entries-view.fxml",this));
    }

    @FXML
    void onLockVaultPressed(ActionEvent event) {

        Database.disconnect();

    }

    @FXML
    void onMinimizeClicked(MouseEvent event) {
        Stage mainStage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        mainStage.setIconified(true);
    }

    @FXML
    void onNewButtonPressed(ActionEvent event) {
        setCenterView((CenterView)loadView("/gui/res/view/entry-edit-view.fxml",this));
    }

    @FXML
    void onPasswordGeneratorPressed(ActionEvent event) {

    }

    @FXML
    void onSyncPressed(ActionEvent event) {
        setCenterView((CenterView)loadView("/gui/res/view/sync-view.fxml",this));
    }

    @FXML
    void onTopBarMouseDragged(MouseEvent event) {
        Stage mainStage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        mainStage.setX(event.getScreenX() - mouseX);
        mainStage.setY(event.getScreenY() - mouseY);
    }

    @FXML
    void onTopBarMousePressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }


    @Override
    public void onChildViewExiting(View child) {

        previousCenterView=(CenterView)child;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        loadLoginView();

    }

    @Override
    public void onSuccess(DatabaseResponse response) {

        if(response.success){
            switch(response.action){
                case DatabaseAction.UPDATE:{
                    currentCenterView.resetView();
                    setCenterView((CenterView)loadView("/gui/res/view/show-entries-view.fxml",this));
                    break;
                } case DatabaseAction.INSERT:{
                    currentCenterView.resetView();
                    break;
                }
            }
        }
    }

    @Override
    public void onFailure(DatabaseResponse response) {


        Alert alert =new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(response.action.toString()+" operation failed");
        alert.setContentText(response.response);
        alert.show();
    }

    public void setCenterView(CenterView view){
        if(currentCenterView!=null)currentCenterView.exitView();
        ((BorderPane)root).setCenter(view.root);
        previousCenterView = currentCenterView;
        currentCenterView = view;


        if(view instanceof LoginView){
            viewLabel.setText("Create or Login");
        }else if(view instanceof EntryEditView){
            viewLabel.setText("Edit");
        }else if(view instanceof SyncView){
            viewLabel.setText("Sync");
        }else if(view instanceof ShowEntriesView){
            viewLabel.setText("Entries");
        }
    }

    @Override
    public void onInitialized() {
        if(previousCenterView == null)
            setCenterView((CenterView)loadView("/gui/res/view/show-entries-view.fxml",this));
        else

            setCenterView(previousCenterView);


        onLog(false);
    }

    public void onLog(boolean out){
        newButton.setDisable(out);
        lockButton.setDisable(out);
        showButton.setDisable(out);
        syncButton.setDisable(out);
    }

    private void loadLoginView(){
        setCenterView((CenterView)loadView("/gui/res/view/login-view.fxml",this));


        onLog(true);
    }

    @Override
    public void onDisconnected() {
        if(Thread.currentThread().getName().equals(Main.UI_THREAD))
            loadLoginView();
        else
            Platform.runLater(this::loadLoginView);

    }



}
