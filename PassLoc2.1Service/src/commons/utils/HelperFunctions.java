package commons.utils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class HelperFunctions {

    public static boolean isInvalidData(String data){
        return data==null || data.isEmpty();
    }

    private static final byte[] PRIMES = {2,3,5,6,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127};

    // taken from https://stackoverflow.com/a/59049496
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    public static String sha256(String text){
        return sha256(text.getBytes());
    }

    public static String sha256(byte[] data){


        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] digest = messageDigest.digest(data);


        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }

        return hashtext ;
    }

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(data);
            out.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }finally{
            try {
                baos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return baos.toByteArray();
    }

    public static byte[] compress2(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[1024];
        int compressedDataLength;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            while (!deflater.finished()) {
                compressedDataLength = deflater.deflate(buffer);
                outputStream.write(buffer, 0, compressedDataLength);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decompress(byte[] bytes) {
        InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int len;
            while((len = in.read(buffer))>0)
                baos.write(buffer, 0, len);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }finally{
            try {
                baos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static String removeChar(String s,char r){
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for(char c:s.toCharArray())
            if(c == r)
                if(!found)found = true;
                else sb.append(c);
            else sb.append(c);

        return sb.toString();
    }
    public static boolean primeByte(byte b){

        if (b<0) b *= -1;
        for(byte prime:PRIMES)
            if (b == prime)
                return true;

        return false;

    }

    public static String derivePasswordKey(char[] passwordArray,String text){
        StringBuilder key = new StringBuilder(text);
        StringBuilder password = new StringBuilder();
        for(char c:passwordArray) {
            if (Character.isDigit(c))
                key.append(c);
            else password.append(c);
        }


        key.append(password.substring(text.length() % password.length()));



        return HelperFunctions.sha256(key.toString());
    }




}
