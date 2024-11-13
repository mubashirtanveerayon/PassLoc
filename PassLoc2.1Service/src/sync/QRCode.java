package sync;

import com.google.zxing.NotFoundException;
import commons.model.SimpleEntry;
import commons.sqlcomm.SQLCom;
import commons.utils.StringCompressor;
import sync.qr.QRCodeGenerator;
import sync.qr.QRCodeReader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class QRCode {

    public static final int  DEFAULT_WIDTH = 400,DEFAULT_HEIGHT = 400;

    public static ArrayList<SimpleEntry>readEntries(ArrayList<BufferedImage> qrImages, SQLCom sqlCom) throws NotFoundException, IOException {

        ArrayList<String>partitions = new ArrayList<>();
        for(BufferedImage qr:qrImages)
            partitions.add(QRCodeReader.readQR(qr));

        byte[] encryptedData = QRCodeReader.loadByteArrayFromPartition(partitions);
        byte[] compressedData = sqlCom.decrypt(encryptedData);

        String jsonArray = StringCompressor.decompress(compressedData);


        return  SimpleEntry.fromJSONArray(jsonArray);
    }


    public static ArrayList<BufferedImage>generateQRCodes(ArrayList<SimpleEntry>entries, SQLCom sqlCom,String signature,int width,int height) throws Exception{
        String jsonArray = SimpleEntry.convertToJSONString(entries);
        byte[] compressed = StringCompressor.compress(jsonArray);
        byte[] encryptedData = sqlCom.encrypt(compressed);
        ArrayList<String>partitions = QRCodeGenerator.createPartitionFromData(encryptedData,QRCodeGenerator.MAX_CHUNK_SIZE,signature);

        return QRCodeGenerator.generateQRImages(partitions,width,height);
    }

    public static ArrayList<BufferedImage>generateQRCodes(ArrayList<SimpleEntry>entries, SQLCom sqlCom,String signature)throws Exception{
        return generateQRCodes(entries,sqlCom,signature,DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

}
