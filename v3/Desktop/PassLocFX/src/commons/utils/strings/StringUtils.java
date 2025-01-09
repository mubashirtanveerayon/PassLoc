package commons.utils.strings;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class StringUtils {

    // Compresses a large string into a byte array
    public static byte[] compress(String data) {
        try {
            byte[] input = data.getBytes(StandardCharsets.UTF_8); // Convert string to byte array
            Deflater deflater = new Deflater();
            deflater.setInput(input);
            deflater.finish();

            // Using ByteArrayOutputStream to collect compressed data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
            byte[] buffer = new byte[1024]; // Buffer for reading compressed data

            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            return outputStream.toByteArray(); // Return compressed byte array
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Decompresses a byte array back to the original string
    public static String decompress(byte[] compressedData) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);
            byte[] buffer = new byte[1024]; // Buffer for reading decompressed data

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8); // Convert byte array back to string
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toB64(String data){
        return toB64(data.getBytes());
    }

    public static String toB64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] fromB64(String data){
        return Base64.getDecoder().decode(data);
    }

}
