package controllers;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import helper.DatabaseEventListener;
import helper.Info;
import helper.NotificationCenter;
import helper.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import org.json.JSONArray;
import services.database.CommandGenerator;
import services.database.Database;
import services.generator.qr.QRCodeGenerator;
import services.generator.qr.QRCodeReader;
import services.model.EntryModel;
import services.secure.AES256WithPassword;
import services.secure.Credential;
import utils.HelperFunctions;
import utils.StringCompressor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;


public class SyncView extends View implements Initializable {

    @FXML
    private Label qrCounterLabel;

    @FXML
    private ImageView qrImageView;

    ArrayList<BufferedImage> qrImages;
    int imageIndex = 0;

    private BufferedImage placeHolderQRCode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.SYNC;
        qrImages = new ArrayList<>();


        try{
            placeHolderQRCode = ImageIO.read(getClass().getResource("/res/image/github.png"));
        }catch(Exception e){
            NotificationCenter.sendFailureNotification(e.getMessage());
            e.printStackTrace();
        }

        loadImageView();
        placeFooter();

    }

    private void placeFooter() {


        if(qrImages.isEmpty()){
            if(placeHolderQRCode != null)
                qrCounterLabel.setText("Scan the QR code for project GitHub link!");
            return;
        }

        qrCounterLabel.setText((imageIndex+1)+"/"+qrImages.size());

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
    void onNextButtonAction(ActionEvent event) {
        if(qrImages.isEmpty())
            return;
        imageIndex = Math.min(imageIndex + 1, qrImages.size()-1);
        loadImageView();
        placeFooter();
    }

    @FXML
    void onPreviousButtonAction(ActionEvent event) {
        if(qrImages.isEmpty())
            return;
        imageIndex = Math.max(imageIndex - 1, 0);
        loadImageView();
        placeFooter();

    }

    @FXML
    void onExportAction(ActionEvent event) {
        if(!Database.online()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }else if( qrImages.isEmpty()){
            NotificationCenter.sendFailureNotification("No QR code has been generated.");
            return;
        }

        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        folderChooser.setTitle("Select folder to save the QR Codes in");
        String saveDirectory = folderChooser.showDialog(null).getAbsolutePath();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddsshhmmss", Locale.getDefault());
        String time = sdf.format(new Date());
        String tableName = Database.getInstance().getTableName();
        String signature = tableName.substring(tableName.length()-4);
        try {
            for (int i = 0; i < qrImages.size(); i++)
                ImageIO.write(qrImages.get(i), "png", new File(saveDirectory + File.separator +String.format("%s_%s_%s.png",i,signature,time)));




            NotificationCenter.sendSuccessNotification("QR codes saved.");

        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @FXML
    void onImportAction(ActionEvent event) {

        if(!Database.online()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }

        FileChooser imageChooser = new FileChooser();
        imageChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        imageChooser.setTitle("Select QR Codes");
        List<File> imageFileList = imageChooser.showOpenMultipleDialog(null);


        try {
            ArrayList<String>partitions = QRCodeReader.createPartitionFromQRImages(imageFileList.toArray(new File[0]));
            String encryptedData = StringCompressor.decompress( QRCodeReader.loadByteArrayFromPartition(partitions));
            Database db = Database.getInstance();
            String json = Credential.getInstance().decrypt(encryptedData);



            ArrayList<EntryModel> entries = EntryModel.fromJSONArray(json);




            for(EntryModel newEntry:entries)
                db.insert(newEntry);


            FXMLLoader dataViewLoader = new FXMLLoader(getClass().getResource("/res/view/data-view.fxml"));
            borderPane.setCenter(dataViewLoader.load());
            DataView controller = dataViewLoader.getController();
            controller.setBorderPane(borderPane);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }


    @FXML
    void onLoadAction(ActionEvent event) {

        if(!Database.online()) {
            NotificationCenter.sendFailureNotification("Database is offline.");
            return;
        }


        Database db = Database.getInstance();

        ArrayList<EntryModel> entries = db.getAllData();

        String json = EntryModel.convertToJsonString(entries);



        String signature = db.getTableName();

        String encryptedData = Credential.getInstance().encrypt(json);

        byte[] compressed = StringCompressor.compress( encryptedData);



        ArrayList<String>partitions = QRCodeGenerator.createPartitionFromData(compressed,2000,signature);
        qrImages.clear();
        try {
            qrImages.addAll(QRCodeGenerator.generateQRImages(partitions,400,400));
            imageIndex = 0;

            loadImageView();
            placeFooter();

        } catch (WriterException e) {
            e.printStackTrace();
        }


    }
}
