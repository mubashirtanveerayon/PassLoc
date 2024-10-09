import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        stage.initStyle(StageStyle.UNDECORATED);

        FXMLLoader mainViewLoader = new FXMLLoader(getClass().getResource("/res/view/main_view.fxml"));

        Scene scene = new Scene(mainViewLoader.load());



        stage.setTitle("PassLoc2.0");


        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {


        launch(args);
    }
}
