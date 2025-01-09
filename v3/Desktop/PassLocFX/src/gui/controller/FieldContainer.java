package gui.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.features.Component;
import gui.features.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class FieldContainer extends Component {


    @FXML
    private TextField keyField,valueTextField;

    @FXML
    private PasswordField valuePasswordField;

    @FXML
    private FontAwesomeIconView valueVisibilityIcon;

    @FXML
    void onValueVisibilityIconClicked(MouseEvent event) {


        View.onPasswordVisibilityIconClicked(valueTextField,valuePasswordField,valueVisibilityIcon);
    }

    public String getKey(){
        return keyField.getText();
    }


    public String getValue(){
        return valueTextField.isVisible()?valueTextField.getText():valuePasswordField.getText();
    }

    @FXML
    void onDeleteFieldClicked(MouseEvent event) {
        remove();
    }

    public void setField(Map.Entry<String, String> entry) {
        keyField.setText(entry.getKey());
        valuePasswordField.setText(entry.getValue());
    }

    @FXML
    void onKeyFieldAction(ActionEvent event) {
        if(valueTextField.isVisible()) valueTextField.requestFocus();
        else  valuePasswordField.requestFocus();

    }
}

