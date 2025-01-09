package gui.controller;

import gui.features.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class TagChip extends Component {


    @FXML
    private Label tagLabel;

    @FXML
    void onDeleteTagClicked(MouseEvent event) {
        remove();
    }

    public void setTag(String tag){
        tagLabel.setText(tag);
    }

    public String getTag(){
        return tagLabel.getText();
    }
}
