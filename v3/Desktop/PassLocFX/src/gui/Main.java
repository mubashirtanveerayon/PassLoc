package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static final String UI_THREAD = "UI";
    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setName(UI_THREAD);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/gui/res/view/main-view.fxml"));
        try{
            Scene scene= new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
