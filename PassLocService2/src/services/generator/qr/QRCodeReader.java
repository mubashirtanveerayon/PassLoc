package services.generator.qr;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import utils.exception.InvalidChunkException;
import utils.exception.MissingChunkException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class QRCodeReader {



    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
    public static String readQR(BufferedImage bufferedImage) throws IOException, NotFoundException {
//        BufferedImage resizedImage = resize(bufferedImage,400,400);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));

        Result result = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();
    }
    public static ArrayList<String> createPartitionFromQRImages(File[] qrFiles) throws NotFoundException, IOException {
        ArrayList<String>partitions = new ArrayList<>();
        for(File qr:qrFiles)
            partitions.add(readQR(ImageIO.read(new FileInputStream(qr))));
        return partitions;
    }

    public static ArrayList<String> createPartitionFromQRImages(ArrayList<BufferedImage> qrImages) throws NotFoundException, IOException {
        ArrayList<String>partitions = new ArrayList<>();
        for(BufferedImage qr:qrImages)
            partitions.add(readQR(qr));
        return partitions;
    }







    public static byte[] loadByteArrayFromPartition(ArrayList<String> partitions) throws InvalidChunkException, MissingChunkException {



        ArrayList<String> copy = new ArrayList<>(partitions);


        StringBuilder base64Encoded = new StringBuilder();

        int totalChunks = copy.size();

        String signature = copy.get(0).split(QRCodeGenerator.CHUNK_DATA_SEPARATOR)[2];

        for(int chunk = 0;chunk < totalChunks;chunk++) {
            String chunkData=null;

            int indexToRemove=-1;
            for(int i=0;i<copy.size();i++){
                String part = copy.get(i);
                String[] parts = part.split(QRCodeGenerator.CHUNK_DATA_SEPARATOR);
                if(parts.length != 3 )throw new InvalidChunkException(chunk,part);

                if(parts[1].equals(Integer.toString(chunk)) && parts[2].equals(signature)){
                    chunkData = part;
                    indexToRemove = i;
                    break;
                }
            }

            if(indexToRemove>-1)copy.remove(indexToRemove);

            if(chunkData == null)

                throw new MissingChunkException(chunk);

            if(chunkData.isEmpty())throw new InvalidChunkException(chunk);
            String[] parts = chunkData.split(QRCodeGenerator.CHUNK_DATA_SEPARATOR);

            base64Encoded.append(parts[0]);




        }


        Base64.Decoder decoder =  Base64.getDecoder();


        String base64EncodedString = base64Encoded.toString();

        byte[] byteArray = decoder.decode(base64EncodedString);


        return byteArray;
    }

    public static void createFileFromPartition(ArrayList<String> partitions, String outputDirectory) throws IOException, InvalidChunkException, MissingChunkException {


        byte[] byteArray = loadByteArrayFromPartition(partitions);



        String fileName = partitions.get(0).split(QRCodeGenerator.CHUNK_DATA_SEPARATOR)[2];



        File outputFile = new File((outputDirectory.endsWith(File.separator) ? outputDirectory:outputDirectory+File.separator)+fileName);
        try(FileOutputStream os = new FileOutputStream(outputFile)){

            os.write(byteArray);
            os.flush();

        }


    }

}
