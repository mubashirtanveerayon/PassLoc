package gui.controller;

import commons.model.Entry;
import database.sync.QRCode;
import database.Database;
import gui.features.CenterView;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class SyncView extends CenterView {



    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Label qrFooterLabel;

    @FXML
    private Label qrImageLabel;

    @FXML
    private ImageView qrImageView;


    ArrayList<BufferedImage> qrImages = new ArrayList<>();
    int imageIndex = -1;

    @FXML
    void onNextPressed(ActionEvent event) {
        imageIndex = Math.min(qrImages.size()-1,imageIndex+1);
        loadImageView();
        loadFooter();

    }

    @FXML
    void onPreviousPressed(ActionEvent event) {

        imageIndex = Math.max(imageIndex-1,0);
        loadImageView();
        loadFooter();
    }

    @FXML
    void onQRImageViewClicked(MouseEvent event) {

        if(imageIndex>=0)return;

        ArrayList<Entry> entries= Database.getInstance().getAll();



        try {
            qrImages = QRCode.generateQRCodes(entries,Database.getInstance().getPasswordMonitor().getPassword());
            imageIndex = 0;
            qrFooterLabel.setVisible(true);
            qrImageLabel.setVisible(false);
            loadImageView();
            loadFooter();
            loadNavButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void loadNavButtons() {
        prevButton.setVisible(imageIndex>=0);
        nextButton.setVisible(imageIndex>=0);
    }


    private void loadImageView() {
        qrImageView.setImage(SwingFXUtils.toFXImage(qrImages.get(imageIndex), null));
    }

    private void loadFooter(){
//        if(qrImages.isEmpty()){
//            qrFooterLabel.setVisible(false);
//            qrImageLabel.setVisible(true);
//            return;
//        }

        qrFooterLabel.setText(String.format("QR codes: %s/%s",(imageIndex+1),qrImages.size()));
    }

    @Override
    public void resetView() {

        qrImageView.setImage(null);
        qrImageLabel.setVisible(true);
        qrFooterLabel.setVisible(false);
        qrImages.clear();
        imageIndex = -1;
        prevButton.setVisible(false);
        loadNavButtons();


    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onImportPressed(ActionEvent event) {

        FileChooser imageChooser = new FileChooser();
        imageChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        imageChooser.setTitle("Select QR Codes");
        List<File> imageFileList = imageChooser.showOpenMultipleDialog(null);
        if(imageFileList.isEmpty())return;

        try {
            ArrayList<BufferedImage>images = new ArrayList<>();
            for(File file : imageFileList)
                images.add(ImageIO.read(file));


            ArrayList<Entry>entries = QRCode.readEntries(images,Database.getInstance().getPasswordMonitor().getPassword());

            Database instance = Database.getInstance();
            for(Entry entry:entries)
                if(instance.exists(entry.creationTime))
                    instance.put(entry);
                else
                    instance.insert(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onExportPressed(ActionEvent event) {

        if (qrImages.isEmpty()) return;

        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        folderChooser.setTitle("Select folder to save the QR Codes in");
        String saveDirectory = folderChooser.showDialog(null).getAbsolutePath();
        if(saveDirectory.isBlank())return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddsshhmmss");
        String time = sdf.format(new Date());

        try {
            for (int i = 0; i < qrImages.size(); i++)
                ImageIO.write(qrImages.get(i), "png", new File(saveDirectory + File.separator + String.format("%s_%s.png", i, time)));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
