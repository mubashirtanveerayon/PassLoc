/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;


import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import helper.State;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import services.secure.Credential;
import utils.PasswordValidator;

/**
 *
 * @author admin
 */
public class UnlockView extends View implements Initializable{



    
    @FXML
    PasswordField passwordField;
    
    @FXML
    TextField textField;
   
    
    @FXML
    MaterialIconView showPasswordImage;






    public void onShowPasswordImageClicked(MouseEvent me){
        if(passwordField.isVisible()){
            showPasswordImage.setGlyphName("VISIBILITY");
            String text =passwordField.getText();
            passwordField.setText("");
            passwordField.setVisible(false);
            textField.setVisible(true);
            textField.setText(text);
        }else{
            showPasswordImage.setGlyphName("VISIBILITY_OFF");
            String text = textField.getText();
            textField.setText("");
            textField.setVisible(false);
            passwordField.setVisible(true);
            passwordField.setText(text);
        }
    }
    
    public void onAccessButtonAction(ActionEvent actionEvent) {

        String password = passwordField.isVisible() ? passwordField.getText() : textField.getText();

        Credential.getInstance(password);
        
        
        FXMLLoader dbLoginLoader = new FXMLLoader(UnlockView.class.getResource("/res/view/db-login-view.fxml"));
        try {
            Parent root = dbLoginLoader.load();

            borderPane.setCenter(root);




            LoginDBView controller = dbLoginLoader.getController();
            controller.setBorderPane(borderPane);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        

        
        
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.MASTER_LOGIN;
    }
}
