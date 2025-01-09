package gui.features;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.controller.MainView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public abstract class View implements Initializable {

    protected View parent; // parent of this view

    @FXML
    public Parent root;

    public void setParent(View parent){
        this.parent = parent;
    }

    public abstract void onChildViewExiting(View child);

    public static void onPasswordVisibilityIconClicked(TextField tf, PasswordField pf, FontAwesomeIconView icon) {
        boolean isHidden = icon.getGlyphName().contains("SLASH");
        pf.setVisible(!isHidden);
        tf.setVisible(isHidden);
        if(isHidden){
            tf.setText(pf.getText());
            pf.setText("");

            icon.setGlyphName("EYE");
        }else{
            pf.setText(tf.getText());
            tf.setText("");

            icon.setGlyphName("EYE_SLASH");
        }
    }

    public static View loadView(String path,View parent){
        FXMLLoader loader = new FXMLLoader(View.class.getResource(path));
        try{
            loader.load();
            View controller=loader.getController();
            controller.setParent(parent);
            return controller;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public View getParent(){
        return parent;
    }
}
