package gui.controller;

import auto.Preload;
import database.Database;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.features.CenterView;
import gui.features.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginView extends CenterView {


    @FXML
    private TextField databasePathField;

    @FXML
    private PasswordField masterPasswordField;

    @FXML
    private TextField masterTextField;

    @FXML
    private FontAwesomeIconView masterVisibilityIcon;


    @FXML
    void onLoginPressed(ActionEvent event) {

        String databasePath = databasePathField.getText();
        Preload.DB_SAVE_LOCATION = databasePath;
        String masterPassword =masterTextField.isVisible()?masterTextField.getText(): masterPasswordField.getText();


        if(databasePath.isBlank() || masterPassword.length()<4){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Database name, path and a password of at least 4 characters are required");
            alert.showAndWait();
            return;
        }

        if(!databasePath.endsWith(Preload.FILE_EXTENSION))databasePath+=Preload.FILE_EXTENSION;
        try{
            Database.establishConnection(databasePath,masterPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    void onMasterVisibilityIconClicked(MouseEvent event) {
        onPasswordVisibilityIconClicked(masterTextField,masterPasswordField,masterVisibilityIcon);
    }

    @FXML
    void onOpenFileClicked(MouseEvent event) {

//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setInitialDirectory(new File(Preload.DB_SAVE_LOCATION));
//        directoryChooser.setTitle("Location");
//        File folder = directoryChooser.showDialog(null);
//        if(folder != null){
//            String dbLocation = folder.getAbsolutePath();
//            databasePathField.setText(dbLocation);
//
//
//        }


        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(Preload.DB_SAVE_LOCATION));
        chooser.getExtensionFilters().clear();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Preload.FILE_EXTENSION.substring(1), "*"+Preload.FILE_EXTENSION));
        chooser.setTitle("Open or create new database file");
        File file = chooser.showSaveDialog(null);
        if(file == null)return;

        databasePathField.setText(file.getAbsolutePath());

    }


    @Override
    public void resetView() {

        masterTextField.clear();
        masterPasswordField.clear();
        masterTextField.setVisible(false);
        masterPasswordField.setVisible(true);
        masterVisibilityIcon.setGlyphName("EYE_SLASH");

        databasePathField.setText(  Preload.DB_SAVE_LOCATION);

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetView();
    }
}
