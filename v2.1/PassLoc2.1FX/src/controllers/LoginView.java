package controllers;

import commons.services.sqlcomm.CommandGenerator;
import commons.utils.Identifier;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.Info;
import helper.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import services.database.Database;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginView extends View implements Initializable {


    @FXML
    private PasswordField dbPasswordField;

    @FXML
    private TextField dbPasswordTextField;

    @FXML
    private TextField locationField;

    @FXML
    private PasswordField masterPasswordField;

    @FXML
    private TextField masterPasswordTextField;

    @FXML
    private TextField nameField;

    @FXML
    MaterialIconView dbPasswordVisibilityIcon,masterPasswordVisibilityIcon;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        nameField.setText("");
        locationField.setText(Info.DB_SAVE_LOCATION);
        dbPasswordField.setText("");
        masterPasswordField.setText("");
        masterPasswordTextField.setText("");
        dbPasswordTextField.setText("");

        State.currentState = State.AppState.DB_LOGIN;
        dbPasswordVisibilityIcon.setGlyphName("VISIBILITY_OFF");
        masterPasswordVisibilityIcon.setGlyphName("VISIBILITY_OFF");

        toggleMasterPasswordVisibility(true);
        toggleDBPasswordVisibility(true);


    }

    private void toggleMasterPasswordVisibility(boolean isPasswordVisible) {

        if(isPasswordVisible){
            String password = masterPasswordTextField.getText();
            masterPasswordTextField.setText("");
            masterPasswordTextField.setVisible(false);

            masterPasswordField.setVisible(true);
            masterPasswordField.setText(password);

            masterPasswordVisibilityIcon.setGlyphName("VISIBILITY_OFF");
        }else{
            String password = masterPasswordField.getText();
            masterPasswordField.setText("");
            masterPasswordField.setVisible(false);

            masterPasswordTextField.setVisible(true);
            masterPasswordTextField.setText(password);

            masterPasswordVisibilityIcon.setGlyphName("VISIBILITY");
        }
    }


    private void toggleDBPasswordVisibility(boolean isPasswordVisible) {
        if(isPasswordVisible){
            String password = dbPasswordTextField.getText();
            dbPasswordTextField.setText("");
            dbPasswordTextField.setVisible(false);

            dbPasswordField.setVisible(true);
            dbPasswordField.setText(password);

            dbPasswordVisibilityIcon.setGlyphName("VISIBILITY_OFF");
        }else{
            String password = dbPasswordField.getText();
            dbPasswordField.setText("");
            dbPasswordField.setVisible(false);

            dbPasswordTextField.setVisible(true);
            dbPasswordTextField.setText(password);

            dbPasswordVisibilityIcon.setGlyphName("VISIBILITY");
        }

    }


    public void login(){

        String name = nameField.getText();
        String location = locationField.getText();
        String dbPassword = dbPasswordField.isVisible() ? dbPasswordField.getText() :  dbPasswordTextField.getText();
        String masterPassword = masterPasswordField.isVisible() ? masterPasswordField.getText() :  masterPasswordTextField.getText();

        if(name.isEmpty() || location.isEmpty() || dbPassword.isEmpty() || masterPassword.isEmpty() || name.isBlank() || location.isBlank() || dbPassword.isBlank() || masterPassword.isBlank() ){

            return;

        }

        if(dbPassword.length() < Info.MIN_PASSWORD_LENGTH || masterPassword.length() < Info.MIN_PASSWORD_LENGTH){
            return;
        }


        CommandGenerator.disconnect();
        CommandGenerator.initialize(dbPassword, masterPassword);
        Database.disconnect();

        try {
            Database.establishConnection(name, location, dbPassword);

            if(Database.online()){

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/view/data_view.fxml"));
                borderPane.setCenter(loader.load());
                DataView controller = loader.getController();
                controller.setBorderPane(borderPane);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }






    }

    @FXML
    void onDBPasswordToggleVisibility(MouseEvent event) {

        toggleDBPasswordVisibility(dbPasswordTextField.isVisible());

    }

    @FXML
    void onLoginButtonClicked(ActionEvent event) {
        login();
    }

    @FXML
    void onMasterPasswordToggleVisibility(MouseEvent event) {
         toggleMasterPasswordVisibility(masterPasswordTextField.isVisible());
    }

    @FXML
    void onOpenFolderClicked(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Info.DB_SAVE_LOCATION));
        directoryChooser.setTitle("Location");
        File folder = directoryChooser.showDialog(null);
        if(folder != null){
            String dbLocation = folder.getAbsolutePath();
            locationField.setText(dbLocation);


        }
    }

    @FXML
    void onNameFieldAction(ActionEvent event) {

        locationField.requestFocus();

    }

    @FXML
    public void onLocationFieldAction(ActionEvent event) {

        if(dbPasswordTextField.isVisible())
            dbPasswordTextField.requestFocus();
        else
            dbPasswordField.requestFocus();
    }

    public void onDBPasswordAction(ActionEvent event){
        if(masterPasswordTextField.isVisible())
            masterPasswordTextField.requestFocus();
        else
            masterPasswordField.requestFocus();
    }

    public void onMasterPasswordAction(ActionEvent event){

        login();

    }

}
