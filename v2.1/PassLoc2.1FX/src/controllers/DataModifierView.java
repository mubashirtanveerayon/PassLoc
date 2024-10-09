package controllers;

import com.jfoenix.controls.JFXTextArea;
import commons.services.model.SimpleEntry;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.NotificationCenter;
import helper.State;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import services.database.Database;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DataModifierView extends View implements Initializable {
    SimpleEntry entry;
    @FXML
    private MFXButton actionButton;

    @FXML
    private JFXTextArea noteArea;

    @FXML
    private TextField passwordField;

    @FXML
    private HBox tagContainer;

    @FXML
    private TextField tagField;

    @FXML
    private TextField usernameField;

    @FXML
    TextField urlField;


    ArrayList<TagView> tags = new ArrayList<>();

    public void setData(SimpleEntry entry) {

        this.entry = entry;

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        State.currentState = State.AppState.EDIT;

        if(tags == null)
            tags = new ArrayList<>();
        tags.clear();
        tagContainer.getChildren().clear();
        if(this.entry == null){
            tagContainer.getChildren().clear();
            usernameField.setText("");
            passwordField.setText("");
            noteArea.setText("");

            actionButton.setText("Add");
        }else{
            for(int i=0;i<entry.getTagSize();i++)
                createTag(entry.getTag(i));
            usernameField.setText(entry.getUsername());
            passwordField.setText(entry.getPassword());

            noteArea.setText(entry.getNote());
            urlField.setText(entry.getUrl());
            actionButton.setText("Update");
        }

    }

    private void createTag(String tag){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/view/tag_view.fxml"));
        try{
            HBox root = loader.load();

            tagContainer.getChildren().add(root);
            TagView controller = loader.getController();
            controller.setTag(tag);
            controller.setId(tags.size());
            tags.add(controller);


            MaterialIconView removeIcon = controller.getRemoveIcon();
            removeIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    tags.remove(controller);
                    tagContainer.getChildren().remove(root);

                }
            });



        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @FXML
    void onModifyAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String url = urlField.getText();
        String note = noteArea.getText();

        if(username.isEmpty() || password.isEmpty()||username.isBlank() || password.isBlank())
        {
            NotificationCenter.sendFailureNotification("Username, password and tags are required");
            return;
        }

        if(!Database.online()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }

        // TODO



 
    }

    @FXML
    void onPasswordFieldAction(ActionEvent event) {
        urlField.requestFocus();
    }

    @FXML
    void onTagFieldAction(ActionEvent event) {

        createTag(tagField.getText());
        tagField.setText("");

    }

    @FXML
    void onUsernameFieldAction(ActionEvent event) {

        passwordField.requestFocus();

    }

    public void onUrlFieldAction(ActionEvent event) {
        noteArea.requestFocus();
    }
}
