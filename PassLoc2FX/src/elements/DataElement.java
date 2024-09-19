package elements;

import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import services.model.EntryModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DataElement extends ListCell<EntryModel> implements Initializable {



    @FXML
    private MenuItem copyUsernameMenuItem,copyPasswordMenuItem;

    @FXML
    private MaterialIconView deleteIcon;

    @FXML
    private MaterialIconView editIcon;

    @FXML
    private Label usernameLabel,passwordLabel;

    private EntryModel entry;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }



    public MaterialIconView getDeleteIcon() {
        return deleteIcon;
    }

    public MaterialIconView getEditIcon() {
        return editIcon;
    }

    public EntryModel getEntry() {
        return entry;
    }





    public void setData(EntryModel entry) {
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
