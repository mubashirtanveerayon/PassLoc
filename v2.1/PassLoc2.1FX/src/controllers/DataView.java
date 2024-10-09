package controllers;


import commons.services.model.SimpleEntry;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.NotificationCenter;
import helper.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.database.Database;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;

import java.util.*;

public class DataView extends View implements Initializable {


    @FXML
    private VBox entryContainer;

    @FXML
    private HBox tagContainer;

    @FXML
    private TextField tagField;

    ArrayList<TagView> tags = new ArrayList<>();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.VIEW;

        if(!Database.online())
            return;

        if(tags == null)
            tags = new ArrayList<>();

        tags.clear();


    }


    public void showFilteredEntries(){

        clearAllEntries();

        if(!Database.online()){
            NotificationCenter.sendFailureNotification("Database is offline");
            return;
        }

        ArrayList<SimpleEntry> entries = Database.getInstance().getAllData();
        for(SimpleEntry entry : entries){
            for(TagView tag : tags){
                if(entry.containsTag(tag.getTag())){
                    createEntry(entry);
                    break;
                }
            }
        }

    }

    private void createEntry(SimpleEntry entry) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/view/data_element_view.fxml"));
        try{

            Node root = loader.load();
            DataElement controller = loader.getController();
            controller.setData(entry);

            MaterialIconView editIcon = controller.getEditIcon();
            MenuItem copyUsernameMenuItem = controller.getCopyUsernameMenuItem();
            MenuItem copyPasswordMenuItem = controller.getCopyPasswordMenuItem();
            MaterialIconView deleteIcon = controller.getDeleteIcon();

            EventHandler<MouseEvent> actionHandler = new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    Object source = event.getSource();
                    if(source == deleteIcon) {
                        Database.getInstance().delete(entry.getId());
                        entryContainer.getChildren().remove(root);
                    }else if(source == editIcon) {
                       gotoEdit(entry);
                    }
                }
            };

            EventHandler<ActionEvent> copyActionHandler = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    Object source = event.getSource();
                    if(source == copyUsernameMenuItem) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry.getUsername()), null);
                        NotificationCenter.sendSuccessNotification("Username copied to clipboard");
                    }else if(source == copyPasswordMenuItem) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry.getPassword()), null);
                        NotificationCenter.sendSuccessNotification("Password copied to clipboard");
                    }
                }
            };

            editIcon.setOnMouseClicked(actionHandler);
            copyUsernameMenuItem.setOnAction(copyActionHandler);
            copyPasswordMenuItem.setOnAction(copyActionHandler);
            deleteIcon.setOnMouseClicked(actionHandler);

            entryContainer.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void gotoEdit(SimpleEntry entry){
        FXMLLoader editView = new FXMLLoader(getClass().getResource("/res/view/data_modifier_view.fxml"));
        try {
            borderPane.setCenter(editView.load());
            DataModifierView controller = editView.getController();
            controller.setBorderPane(borderPane);
            if(entry!=null)
                controller.setData(entry);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void clearAllEntries() {
        tags.clear();
        tagContainer.getChildren().clear();
        entryContainer.getChildren().clear();
    }

    @FXML
    void onAddNewAction(ActionEvent event) {
        gotoEdit(null);

    }

    @FXML
    void onTagFieldAction(ActionEvent event) {



        String tag = tagField.getText();
        if(tag.isEmpty() || tag.isBlank())
            return;


        tagField.setText("");

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
}
