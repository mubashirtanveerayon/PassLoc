package database.sync;

import commons.model.Entry;
import database.sync.qr.QRCodeGenerator;
import database.sync.qr.QRCodeReader;
import commons.utils.HelperFunctions;
import commons.utils.cipher.AES256;


import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class QRCode {

    public static final int  DEFAULT_WIDTH = 400,DEFAULT_HEIGHT = 400;

    public static ArrayList<Entry>readEntries(ArrayList<BufferedImage> qrImages,char[]password) throws Exception {

        ArrayList<String>partitions = new ArrayList<>();
        for(BufferedImage qr:qrImages)
            partitions.add(QRCodeReader.readQR(qr));

        byte[] encryptedData = QRCodeReader.loadByteArrayFromPartition(partitions);
        byte[] compressedData = AES256.decrypt(encryptedData, password);

        return Entry.fromCompressedArray(compressedData);
    }


    public static ArrayList<BufferedImage>generateQRCodes(ArrayList<Entry>entries, char[]password,int width,int height) throws Exception{

        byte[] compressed = Entry.toCompressedArray(entries);
        byte[] encryptedData = AES256.encrypt(compressed,password);
        String signature = HelperFunctions.sha256( HelperFunctions.sha256(compressed));
        ArrayList<String>partitions = QRCodeGenerator.createPartitionFromData(encryptedData,QRCodeGenerator.MAX_CHUNK_SIZE,signature);

        return QRCodeGenerator.generateQRImages(partitions,width,height);
    }

    public static ArrayList<BufferedImage>generateQRCodes(ArrayList<Entry>entries,char[] password)throws Exception{
        return generateQRCodes(entries,password,DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

}
