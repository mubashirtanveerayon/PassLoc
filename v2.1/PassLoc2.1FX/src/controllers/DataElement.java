package controllers;

import commons.services.model.SimpleEntry;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class DataElement  {
    @FXML
    private MenuItem copyUsernameMenuItem,copyPasswordMenuItem;

    @FXML
    private MaterialIconView deleteIcon;

    @FXML
    private MaterialIconView editIcon;

    @FXML
    private Label usernameLabel,passwordLabel;

    private SimpleEntry entry;

    public MaterialIconView getDeleteIcon() {
        return deleteIcon;
    }

    public MaterialIconView getEditIcon() {
        return editIcon;
    }

    public SimpleEntry getEntry() {
        return entry;
    }



    public String getId(){
        return entry.getId();
    }


    public void setData(SimpleEntry entry) {
        this.entry = entry;
        usernameLabel.setText(entry.getUsername());
        String password = "";
        for(int i=0;i<entry.getPassword().length();i++)
            password += (char)8226;
        passwordLabel.setText(password);
    }

    public MenuItem getCopyUsernameMenuItem() {
        return copyUsernameMenuItem;
    }

    public MenuItem getCopyPasswordMenuItem() {
        return copyPasswordMenuItem;
    }
}
