package controllers;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import helper.NotificationCenter;
import helper.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jasypt.util.binary.AES256BinaryEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;
import org.json.JSONArray;
import services.database.Database;
import services.generator.qr.QRCodeGenerator;
import services.generator.qr.QRCodeReader;
import services.model.EntryModel;
import services.secure.Credential;
import utils.HelperFunctions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

public class SyncView extends View implements Initializable {

    @FXML
    private Label qrCounterLabel;

    @FXML
    private ImageView qrImageView;

    ArrayList<BufferedImage> qrImages;
    int imageIndex = 0;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.SYNC;
        qrImages = new ArrayList<>();
        try {
            qrImages.add(ImageIO.read(getClass().getResource("/res/image/placeholderqr.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        placeFooter();
    }

    private void placeFooter() {


        if(qrImages.isEmpty())
            return;
        qrCounterLabel.setText((imageIndex+1)+"/"+qrImages.size());

    }

    private void loadImageView(){
        if(qrImages.isEmpty())
            return;
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


        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        folderChooser.setTitle("Select folder to save the QR Codes in");
        String saveDirectory = folderChooser.showDialog(null).getAbsolutePath();
        try {
            for (int i = 0; i < qrImages.size(); i++) {
                ImageIO.write(qrImages.get(i), "png", new File(saveDirectory + File.separator + i + ".png"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @FXML
    void onImportAction(ActionEvent event) {

        FileChooser imageChooser = new FileChooser();
        imageChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        imageChooser.setTitle("Select QR Codes");
        List<File> imageFileList = imageChooser.showOpenMultipleDialog(null);


        try {
            ArrayList<String>partitions = QRCodeReader.createPartitionFromQRImages(imageFileList.toArray(new File[0]));
            byte[] encryptedData = QRCodeReader.loadByteArrayFromPartition(partitions);
            Database db = Database.getInstance();
            String json = db.decryptText(new String(encryptedData));

            ArrayList<EntryModel> entries = EntryModel.fromJSONArray(new JSONArray(json));

            for(EntryModel entry : entries){
                String id = entry.getId();
                if(id == null || !db.alreadyExists(id))
                    db.insert(entry);
                else
                    db.update(entry);
            }

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

        String json = HelperFunctions.convertToJsonString(Database.getInstance().getAllData());
        Database db = Database.getInstance();

        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(Credential.getInstance().getHashedMasterPassword());
        String sign = HelperFunctions.sha256(db.getName());
        String encryptedData = db.encryptText(json);
        ArrayList<String>partitions = QRCodeGenerator.createPartitionFromData(encryptedData.getBytes(),2000,sign);
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
