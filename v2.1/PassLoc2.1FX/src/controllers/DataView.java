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
import javafx.scene.Parent;
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

    ArrayList<String> tags = new ArrayList<>();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.VIEW;

        if(!Database.online())
            return;

        if(tags == null)
            tags = new ArrayList<>();

        tags.clear();

        tagContainer.getChildren().clear();


    }


    public void showFilteredEntries(){
        if (tags.isEmpty())
            return;

        entryContainer.getChildren().clear();

        if(!Database.online()){
            NotificationCenter.sendFailureNotification("Database is offline");
            return;
        }

        ArrayList<SimpleEntry> entries = Database.getInstance().getAllData();
        for(SimpleEntry entry : entries){

            boolean containsTags = true;

            for(String tag:tags)
                if (!entry.containsTag(tag)) {
                    containsTags = false;
                    break;
                }
            if(containsTags)
                createEntry(entry);
        }

    }

    private void createEntry(SimpleEntry entry) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/view/data_element_view.fxml"));
        try{

            HBox root = loader.load();
            DataElement controller = loader.getController();
            controller.setData(entry);

            MaterialIconView editIcon = controller.getEditIcon();
            MenuItem copyUsernameMenuItem = controller.getCopyUsernameMenuItem();
            MenuItem copyPasswordMenuItem = controller.getCopyPasswordMenuItem();
            MenuItem copyUrlMenuItem = controller.getCopyUrlMenuItem();
            MenuItem copyNoteMenuItem = controller.getCopyNoteMenuItem();
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
                    }else if(source == copyUrlMenuItem) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry.getUrl()), null);
                        NotificationCenter.sendSuccessNotification("URL copied to clipboard");
                    }else if(source == copyNoteMenuItem) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry.getNote()), null);
                        NotificationCenter.sendSuccessNotification("Note copied to clipboard");
                    }
                }
            };

            editIcon.setOnMouseClicked(actionHandler);
            copyUsernameMenuItem.setOnAction(copyActionHandler);
            copyPasswordMenuItem.setOnAction(copyActionHandler);
            copyUrlMenuItem.setOnAction(copyActionHandler);
            copyNoteMenuItem.setOnAction(copyActionHandler);

            deleteIcon.setOnMouseClicked(actionHandler);

            entryContainer.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void gotoEdit(SimpleEntry entry){
        FXMLLoader editView = new FXMLLoader(getClass().getResource("/res/view/data_modifier_view.fxml"));
        try {
            Parent root = editView.load();

            DataModifierView controller = editView.getController();

            controller.setData(entry);
            controller.setBorderPane(borderPane);
//            if(entry!=null)


            borderPane.setCenter(root);
        }catch(Exception ex){
            ex.printStackTrace();
        }
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


            TagView controller = loader.getController();
            controller.setTag(tag);
            controller.setId(tags.size());
            tags.add(tag);


            MaterialIconView removeIcon = controller.getRemoveIcon();
            removeIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    tags.remove(tag);
                    tagContainer.getChildren().remove(root);
                    showFilteredEntries();

                }
            });

            tagContainer.getChildren().add(root);

            showFilteredEntries();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
