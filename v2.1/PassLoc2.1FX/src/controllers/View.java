package controllers;

import javafx.scene.layout.BorderPane;

public class View {

    protected BorderPane borderPane;

    public void setBorderPane(BorderPane borderPane) {
        if(this.borderPane != null)
            this.borderPane = borderPane;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }


}
