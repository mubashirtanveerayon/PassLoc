package controllers;

import services.commons.model.SimpleEntry;
import services.commons.secure.AES256WithSaltAndIV;
import services.commons.sqlcomm.SQLCom;
import helper.NotificationCenter;
import helper.State;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import services.database.Database;
import services.sync.QRCode;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class SyncView extends View implements Initializable {

    @FXML
    private MFXButton generateButton;


    @FXML
    private Label qrCounterLabel;

    @FXML
    private ImageView qrImageView;


    ArrayList<BufferedImage> qrImages = new ArrayList<>();
    int imageIndex = 0;

    private BufferedImage placeHolderQRCode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        State.currentState = State.AppState.VIEW;

        if(qrImages == null)
            qrImages = new ArrayList<>();
        qrImages.clear();

        try{
            placeHolderQRCode = ImageIO.read(getClass().getResource("/res/images/github.png"));
        }catch(Exception e){
            e.printStackTrace();
        }

        generateButton.setText("Generate");

        loadImageView();
        placeFooter();
        generateButton.setText("Generate");
    }


    private void placeFooter() {


        if(qrImages.isEmpty()){
            if(placeHolderQRCode != null)
                qrCounterLabel.setText("Click on Generate to generate QR codes!");
            return;
        }

        qrCounterLabel.setText(String.format("QR codes: %s/%s",(imageIndex+1),qrImages.size()));

    }

    private void loadImageView(){
        if(qrImages.isEmpty()) {

            if(placeHolderQRCode != null)
                qrImageView.setImage(SwingFXUtils.toFXImage(placeHolderQRCode,null));

            return;
        }
        qrImageView.setImage(SwingFXUtils.toFXImage(qrImages.get(imageIndex), null));
    }





    @FXML
    void onGenerateOrSaveAction(ActionEvent event) {
        if (Database.offline()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }
        if(qrImages.isEmpty()) {




            ArrayList<SimpleEntry>entries = Database.getInstance().getAllData();

            String signature = SQLCom.getInstance().getTableName();




            qrImages.clear();


            try {
                qrImages.addAll(QRCode.generateQRCodes(entries, SQLCom.getInstance(),signature));
                imageIndex = 0;

                loadImageView();
                placeFooter();

                generateButton.setText("Export");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }



        }else{


            DirectoryChooser folderChooser = new DirectoryChooser();
            folderChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            folderChooser.setTitle("Select folder to save the QR Codes in");
            String saveDirectory = folderChooser.showDialog(null).getAbsolutePath();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddsshhmmss");
            String time = sdf.format(new Date());
            String tableName = SQLCom.getInstance().getTableName();
            String signature = tableName.substring(tableName.length()-4);
            try {
                for (int i = 0; i < qrImages.size(); i++)
                    ImageIO.write(qrImages.get(i), "png", new File(saveDirectory + File.separator +String.format("%s_%s_%s.png",i,signature,time)));




                NotificationCenter.sendSuccessNotification("QR codes saved.");

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    @FXML
    void onImportAction(ActionEvent event) {

        if(Database.offline()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }

        FileChooser imageChooser = new FileChooser();
        imageChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        imageChooser.setTitle("Select QR Codes");
        List<File> imageFileList = imageChooser.showOpenMultipleDialog(null);


        try {




            ArrayList<BufferedImage>images = new ArrayList<>();
            for(File file : imageFileList)
                images.add(ImageIO.read(file));


            ArrayList<SimpleEntry>entries = QRCode.readEntries(images,SQLCom.getInstance());

            Database instance = Database.getInstance();
            for(SimpleEntry entry:entries)
                instance.insert(entry);




            FXMLLoader dataViewLoader = new FXMLLoader(getClass().getResource("/res/view/data_view.fxml"));
            Parent root = dataViewLoader.load();
            DataView controller = dataViewLoader.getController();
            controller.setBorderPane(borderPane);

            borderPane.setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onLeftAction(ActionEvent event) {
        if(qrImages.isEmpty())
            return;
        imageIndex = Math.max(imageIndex - 1, 0);
        loadImageView();
        placeFooter();
    }

    @FXML
    void onRightAction(ActionEvent event) {
        if(qrImages.isEmpty())
            return;
        imageIndex = Math.min(imageIndex + 1, qrImages.size()-1);
        loadImageView();
        placeFooter();
    }
}
