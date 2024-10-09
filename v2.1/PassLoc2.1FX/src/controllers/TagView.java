package controllers;

import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TagView {

    @FXML
    private MaterialIconView removeIcon;

    @FXML
    private Label tagLabel;


    private int id=-1;



    public MaterialIconView getRemoveIcon() {
        return removeIcon;
    }

    public String getTag() {
        return tagLabel.getText();
    }

    public void setTag(String tag){
        tagLabel.setText(tag);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(id<0)
            this.id = id;
    }
}
