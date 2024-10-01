package com.loc.service.utils;

import com.loc.service.passloc.model.EntryModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class HelperFunctions {

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

    public static String convertToJsonString(ArrayList<EntryModel> entries){
        StringBuilder json = new StringBuilder();
        json.append("[");
        for(int i=0;i<entries.size();i++){
            EntryModel entry = entries.get(i);
            json.append("{\"tag\":\"").append(entry.getTag()).append("\",\"username\":\"").append(entry.getUsername()).append("\",\"password\":\"").append(entry.getPassword()).append("\"");
            if(entry.getId() != null)
                json.append(",\"id\":\"").append(entry.getId()).append("\"");
            json.append("}");
            if(i < entries.size()-1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }


}
