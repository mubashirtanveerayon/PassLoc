package com.loc.service.passloc.generator.qr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.loc.service.utils.exception.InvalidChunkException;
import com.loc.service.utils.exception.MissingChunkException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
public class QRCodeReader {


    public static ArrayList<String> createPartitionFromQRImages(ArrayList<InputStream> qrImages) throws NotFoundException, ChecksumException, FormatException {
        ArrayList<String>partitions = new ArrayList<>();
        for(InputStream qr:qrImages)
            partitions.add(readQR(qr));
        return partitions;
    }



    public static String readQR(InputStream imageStream) throws ChecksumException, NotFoundException, FormatException {
        Bitmap bMap = BitmapFactory.decodeStream(imageStream);
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();

        Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
        decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

        Result result = reader.decode(bitmap, decodeHints);
        return result.getText();
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
