package controllers;

import com.jfoenix.controls.JFXTextArea;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.Info;
import helper.NotificationCenter;
import helper.State;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import services.database.Database;
import services.model.EntryModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DataModifierView extends View implements Initializable {


    EntryModel entry;

    @FXML
    private MFXButton actionButton;

    @FXML
    private JFXTextArea passwordField;

    @FXML
    private TextField tagField;

    @FXML
    private TextField usernameField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.EDIT;
        actionButton.setText("Add");
        tagField.setEditable(true);

    }

    public void setData(EntryModel entry){
        this.entry = entry;
        tagField.setText(entry.getTag());
        usernameField.setText(entry.getUsername());

        passwordField.setText(entry.getPassword());
        actionButton.setText("Update");
        tagField.setEditable(false);
    }

    @FXML
    void onActionButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String tag = tagField.getText();

        if(username.isEmpty() || password.isEmpty() || tag.isEmpty()) {
            NotificationCenter.sendFailureNotification("You cannot leave any field empty.");
            return;
        }


        if(!Database.online()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }

        Database db = Database.getInstance();
        if(entry == null) {

            db.insert(new EntryModel(tag, username, password));

            usernameField.setText("");
            passwordField.setText("");

            usernameField.requestFocus();

        }else {
            db.update(new EntryModel(entry.getId(), tag, username, password));

            FXMLLoader dataViewLoader = new FXMLLoader(getClass().getResource("/res/view/data-view.fxml"));
            try {
                borderPane.setCenter(dataViewLoader.load());
                DataView controller = dataViewLoader.getController();
                controller.setBorderPane(borderPane);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }





    }

    @FXML
    void onTagAction(ActionEvent event) {

        usernameField.requestFocus();
        usernameField.positionCaret(usernameField.getText().length());
    }

    @FXML
    void onUsernameAction(ActionEvent event) {
        passwordField.requestFocus();
        passwordField.positionCaret(passwordField.getText().length());
    }
}
