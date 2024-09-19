package controllers;


import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import elements.DataElement;
import helper.Info;
import helper.State;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.control.SearchableComboBox;
import services.database.Database;
import services.model.EntryModel;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DataView extends View implements Initializable {



    @FXML
    private SearchableComboBox<String> tagComboBox;

    @FXML
    private VBox dataElementContainer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        State.currentState = State.AppState.VIEW;

        if(Database.getInstance() == null)
            return;

        ArrayList<String> tags = Database.getInstance().getAllTags();

        tagComboBox.getItems().addAll(tags);






//        ArrayList<String> tags = new ArrayList<>(
//                Arrays.asList("String 1","string 2","String 3","String 3","String 3","String 3","String 3","String 3","String 3","String 3","String 3","String 3","String 3","String 3")
//        );
//        tagListView.getItems().clear();
//        tagListView.getItems().addAll(tags);
//
//        tagSearchView.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                tagListView.getItems().clear();
//                tagListView.getItems().addAll(searchTags(newValue,tags));
//            }
//        });
//
//        tagSearchView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent event) {
//                elementScrollPane.setVisible(false);
//                tagListView.setVisible(true);
//            }
//        });
//
//        tagListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent event) {
////                System.out.println(tagListView.getSelectionModel().getSelection().values().toArray()[0]);
//
//
//                tagListView.setVisible(false);
//
//                loadData(tagListView.getSelectionModel().getSelection().values().toArray()[0].toString());
//                elementScrollPane.setVisible(true);
//
//            }
//        });

//
////        int row=0,col=0;
//        for(int i=0;i<2;i++){
//            EntryModel entry = new EntryModel("urhgg","user: "+i,"password: "+i);
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/res/view/data-element-view.fxml"));
//            HBox root;
//            try {
//
//                root = fxmlLoader.load();
//                DataElement element = fxmlLoader.getController();
//                element.setData(entry);
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            if(root == null)
//                continue;
//
//
////            GridPane.setMargin(root,new Insets(10));
//
//            dataElementContainer.getChildren().add(root);
//
//
////            if(col == 3){
////                col = 0;
////                row++;
////            }
//
//        }


    }

    private void loadData(final String tag) {
        if(Database.getInstance() == null)
            return;
        dataElementContainer.getChildren().clear();
        ArrayList<EntryModel> entries = Database.getInstance().getEntries(tag);
        for(EntryModel entry : entries) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/res/view/data-element-view.fxml"));
            HBox root=null;
            try {

                root = fxmlLoader.load();
                DataElement element = fxmlLoader.getController();
                element.setData(entry);

                MaterialIconView editIcon = element.getEditIcon();
                MenuItem copyUsernameMenuItem = element.getCopyUsernameMenuItem();
                MenuItem copyPasswordMenuItem = element.getCopyPasswordMenuItem();
                MaterialIconView deleteIcon = element.getDeleteIcon();

                EventHandler<MouseEvent> actionHandler = new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        Object source = event.getSource();
                        if(source == deleteIcon) {
                            Database.getInstance().delete(entry.getId());
                            loadData(tag);
                        }else if(source == editIcon) {
                            FXMLLoader editView = new FXMLLoader(getClass().getResource("/res/view/data-modifier-view.fxml"));
                            try {
                                borderPane.setCenter(editView.load());
                                DataModifierView controller = editView.getController();
                                controller.setBorderPane(borderPane);
                                controller.setData(entry);
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                };

                EventHandler<ActionEvent> copyActionHandler = new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        Object source = event.getSource();
                        if(source == copyUsernameMenuItem) {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry.getUsername()), null);
                        }else if(source == copyPasswordMenuItem) {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry.getPassword()), null);
                        }
                    }
                };

                editIcon.setOnMouseClicked(actionHandler);
                copyUsernameMenuItem.setOnAction(copyActionHandler);
                copyPasswordMenuItem.setOnAction(copyActionHandler);
                deleteIcon.setOnMouseClicked(actionHandler);

            } catch (IOException e) {
               e.printStackTrace();
            }

            if(root == null)
                continue;


            dataElementContainer.getChildren().add(root);

        }


    }

//    public ArrayList<String> searchTags(String search,ArrayList<String> tags){
//        ArrayList<String> filtered = new ArrayList<>();
//
//        for(String tag:tags){
//            if(tag.toLowerCase().contains(search.toLowerCase())){
//                filtered.add(tag);
//            }
//        }
//
//        return filtered;
//    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        FXMLLoader entryViewLoader = new FXMLLoader(getClass().getResource("/res/view/data-modifier-view.fxml"));

        try{
            Parent root = entryViewLoader.load();
            borderPane.setCenter(root);
            DataModifierView controller = entryViewLoader.getController();
            controller.setBorderPane(borderPane);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void onTagComboBoxAction(ActionEvent event) {
//        elementScrollPane.setVisible(true);
//        tagListView.setVisible(false);



        loadData(tagComboBox.getSelectionModel().getSelectedItem());
    }


}
