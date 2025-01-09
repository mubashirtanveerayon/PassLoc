package database.sync.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Base64;

public class QRCodeGenerator {


    public static final int MAX_CHUNK_SIZE = 2800;
    public static final String CHUNK_DATA_SEPARATOR = ":";



    public static ArrayList<String> createPartitionFromData(byte[] data, int chunkSize, String signature){

        String b64EncodedString = Base64.getEncoder().encodeToString(data);
        int dataLength = b64EncodedString.length();

        chunkSize = Math.min(MAX_CHUNK_SIZE,chunkSize);

        ArrayList<String> parts = new ArrayList<>();

        int dataIndex = 0;

        for(int chunk=0;;chunk++){

            String metaData = ":"+Integer.toString(chunk)+":"+signature;



            int reservedSpace = metaData.length();

            boolean isFinalChunk = dataIndex + chunkSize-reservedSpace >= dataLength;

            StringBuilder sb = new StringBuilder();


            if(isFinalChunk){


                for(;dataIndex<dataLength;dataIndex++) sb.append(b64EncodedString.charAt(dataIndex));
                sb.append(metaData);
                parts.add(sb.toString());
                break;
            }else{
                for(int i=0;i<chunkSize-reservedSpace;i++,dataIndex++) sb.append(b64EncodedString.charAt(dataIndex));

                sb.append(metaData);
                parts.add(sb.toString());
            }



        }

        return parts;


    }

    public static BufferedImage getQRImage(String data, int width, int height) throws WriterException {
        return MatrixToImageWriter.toBufferedImage(new QRCodeWriter() .encode(data, BarcodeFormat.QR_CODE,width,height));
    }

    public static ArrayList<BufferedImage> generateQRImages(ArrayList<String>partitions,int width,int height) throws WriterException {
        ArrayList<BufferedImage> images = new ArrayList<>();

        for(int i=0;i<partitions.size();i++)
            images.add(getQRImage(partitions.get(i),width,height));

        return images;
    }




}
