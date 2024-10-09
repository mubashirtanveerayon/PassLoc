package controllers;

import javafx.scene.layout.BorderPane;

public class View {

    protected BorderPane _borderPane;

    public void setBorderPane(BorderPane borderPane) {
        if(this._borderPane != null)
            this._borderPane = borderPane;
    }

    public BorderPane getBorderPane() {
        return _borderPane;
    }


}
