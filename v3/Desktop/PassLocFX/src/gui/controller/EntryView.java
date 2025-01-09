package gui.controller;

import commons.model.Entry;
import gui.features.CenterView;
import gui.features.Component;
import gui.features.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.awt.datatransfer.StringSelection;


public class EntryView extends Component implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        String value = entry.getFieldValue(source.getText());
        copy(value);
    }

    private void copy(String value){
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), null);
    }

    private Entry entry=null;
    @FXML
    private MenuButton copyMenuButton;
    @FXML

    private Label usernameLabel,urlLabel;

    @FXML
    void onCopyPasswordClicked(MouseEvent event) {
        copy(entry.getPassword());
    }

    @FXML
    void onCopyUrlClicked(MouseEvent event) {
        copy(entry.getURL());
    }

    @FXML
    void onCopyUsernameClicked(MouseEvent event) {
        copy(entry.getUsername());
    }

    @FXML
    void onDeleteClicked(MouseEvent event) {
        remove();

        entry.reset();
    }

    @FXML
    void onEditClicked(MouseEvent event) {
        //TODO: create new entry-edit-view ? or load a previously loaded view
        MainView grandParent =(MainView) parent.getParent();
        EntryEditView entryEditView = (EntryEditView)View.loadView("/gui/res/view/entry-edit-view.fxml",grandParent);
        entryEditView.setEntry(entry);
        grandParent.setCenterView(entryEditView);
    }

    public Entry getEntry(){
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
        usernameLabel.setText(entry.getUsername());
        urlLabel.setText(entry.getURL());
        for(String fieldKey:entry.getFieldKeys()){
            MenuItem item = new MenuItem(fieldKey);


            item.setOnAction(this);
            copyMenuButton.getItems().add(item);
        }

    }
}
