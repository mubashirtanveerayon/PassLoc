/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import controllers.UnlockView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import controllers.MainView;
public class Main extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        
        stage.initStyle(StageStyle.UNDECORATED);

//        stage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader mainViewLoader = new FXMLLoader(Main.class.getResource("/res/view/main-view.fxml"));



        Scene scene = new Scene(mainViewLoader.load());
        
        

        stage.setTitle("PassLoc2.0");
        

        stage.setScene(scene);
        stage.show();
        


        
    }

    public static void main(String[] args) {




        launch();
    }

    
    
}
