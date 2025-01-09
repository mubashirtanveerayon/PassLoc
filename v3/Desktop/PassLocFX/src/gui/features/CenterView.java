package gui.features;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public abstract class CenterView extends View {
    

//    public CenterView loadCenterView(String path){
//        CenterView controller = (CenterView)loadView(path,parent);
//        ((BorderPane)parent.root).setCenter(controller.root);
//        return controller;
//    }

    public void exitView(){
        resetView();
        parent.onChildViewExiting(this);
    }
    public abstract void resetView();

    @Override
    public void onChildViewExiting(View child) {

    }
    
}
