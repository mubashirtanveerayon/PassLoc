package com.loc.service.passloc.secure;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class AES256WithPassword {


    private final SecretKey secretKey;
    private final byte[] iv;
    public AES256WithPassword(String password) {

        try {
            secretKey = getKeyFromPassword(password);
            iv = generateFixedIV(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    // Derive a 256-bit AES key from the password without salt
    public static SecretKey getKeyFromPassword(String password) throws Exception {
        // Create a SHA-256 hash of the password to get a 256-bit key
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    // Generate a fixed IV (not random) based on password for simplicity (can be improved)
    public static byte[] generateFixedIV(String password) throws Exception {
        // Use a hash of the password as IV (for simplicity, you can modify this to a more secure solution)
        MessageDigest sha = MessageDigest.getInstance("MD5"); // Use MD5 for 16-byte IV
        return sha.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    // Encrypt the given plaintext using AES-256
    public String encrypt(String plaintext)  {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // AES in CBC mode with PKCS5 padding

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encrypted); // Encode to Base64 for readable output
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // Decrypt the given ciphertext using AES-256
    public String decrypt(String ciphertext)  {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decrypted);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


}