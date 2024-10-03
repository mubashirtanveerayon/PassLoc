/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.DatabaseEventListener;
import helper.Info;
import helper.NotificationCenter;
import helper.State;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import services.database.Database;
import services.generator.password.PasswordValidator;
import services.secure.Credential;
import utils.Identifier;
import utils.dbInterface.DatabaseListener;


public class LoginDBView extends View implements Initializable {

    @FXML
    PasswordField passwordField;

    @FXML
    TextField textField,dbNameField,dbLocationField;


    @FXML
    MaterialIconView showPasswordImage;

    @FXML
    MFXButton accessButton;

    DatabaseEventListener listener = new DatabaseEventListener();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        State.currentState = State.AppState.DB_LOGIN;

        dbLocationField.setText(Info.DB_SAVE_LOCATION);

        dbNameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                String dbLocation = dbLocationField.getText();
                if(dbLocation== null || dbLocation.isEmpty())return;

                String extension = "."+Identifier.DATABASE_EXTENSION;
                File file = new File(dbLocation+File.separator+(newValue.endsWith(extension) ? newValue : newValue+extension));
                if(file.exists()){
                    accessButton.setText("Access");
                }else{
                    accessButton.setText("Create");
                }
            }
        });

        accessButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                login();

            }
        });




    }

    private void login(){
        String name = dbNameField.getText();
        String password = passwordField.isVisible()?passwordField.getText():textField.getText();

        if(name.isEmpty() || password.isEmpty())
            return;

        try{
            PasswordValidator.validate(password);
            if(Database.online()) {
                Database.disconnect();
                Credential.getInstance().resetEncryptor();
            }
            Database.establishConnection(name,dbLocationField.getText(),password);

            Database db = Database.getInstance();
            db.setListener(listener);
            if(db.requireInitialization())
                db.initialize();
            FXMLLoader dataViewLoader = new FXMLLoader(getClass().getResource("/res/view/data-view.fxml"));
            Parent root = dataViewLoader.load();
            borderPane.setCenter(root);

            DataView controller = dataViewLoader.getController();
            controller.setBorderPane(borderPane);


        }catch(Exception e){
            e.printStackTrace();

            NotificationCenter.sendFailureNotification(e.getMessage());
        }
    }

    public void onShowPasswordImageClicked(MouseEvent me){
        if(passwordField.isVisible()){
            showPasswordImage.setGlyphName("VISIBILITY");
            String text =passwordField.getText();
            passwordField.setText("");
            passwordField.setVisible(false);
            textField.setVisible(true);
            textField.setText(text);
        }else{
            showPasswordImage.setGlyphName("VISIBILITY_OFF");
            String text = textField.getText();
            textField.setText("");
            textField.setVisible(false);
            passwordField.setVisible(true);
            passwordField.setText(text);
        }
    }


    public void onSaveLocationImageClicked(MouseEvent me){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Info.DB_SAVE_LOCATION));
        directoryChooser.setTitle("Location");
        File folder = directoryChooser.showDialog(null);
        if(folder != null){
            String dbLocation = folder.getAbsolutePath();
            dbLocationField.setText(dbLocation);


            String dbName = dbNameField.getText();
            if(dbName == null || dbName.isEmpty())return;

            String extension = "." + Identifier.DATABASE_EXTENSION;
            File file = new File(dbLocation+File.separator+(dbName.endsWith(extension) ? dbName : dbName+extension));
            if(file.exists()){
                accessButton.setText("Access");
            }else{
                accessButton.setText("Create");
            }
        }
    }

    @FXML
    void onDatabaseLocationAction(ActionEvent event) {
        if(passwordField.isVisible())
            passwordField.requestFocus();
        else
            textField.requestFocus();
    }

    @FXML
    void onDatabaseNameAction(ActionEvent event) {
        dbLocationField.requestFocus();
        dbLocationField.positionCaret(dbLocationField.getText().length());
    }

    @FXML
    void onPasswordAction(ActionEvent event) {
        login();
    }
}
