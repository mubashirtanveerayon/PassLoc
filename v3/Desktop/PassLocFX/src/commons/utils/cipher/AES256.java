package commons.utils.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES256 {

    private static final int KEY_SIZE = 256;
    private static final int SALT_SIZE = 16;
    private static final int IV_SIZE = 16; // 16 bytes for AES block size
    private static final int ITERATION_COUNT = 65536;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static byte[]FIXED_SALT = "PassLocv3".getBytes();

    public static byte[] encrypt(byte[] plainData, char[] password) throws Exception {
        byte[] salt = generateRandomBytes(SALT_SIZE);
        byte[] iv = generateRandomBytes(IV_SIZE);

        SecretKey key = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        byte[] encryptedText = cipher.doFinal(plainData);

        // Concatenate salt, IV, and encrypted text and encode as Base64
        byte[] encryptedData = new byte[salt.length + iv.length + encryptedText.length];
        System.arraycopy(salt, 0, encryptedData, 0, salt.length);
        System.arraycopy(iv, 0, encryptedData, salt.length, iv.length);
        System.arraycopy(encryptedText, 0, encryptedData, salt.length + iv.length, encryptedText.length);

        return Base64.getEncoder().encode(encryptedData);
    }

    public static String encrypt(String plainText,char[]password)throws Exception{
        return new String(encrypt(plainText.getBytes(),password));
    }

    public static byte[] decrypt(byte[] encryptedData, char[] password) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);

        byte[] salt = new byte[SALT_SIZE];
        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedText = new byte[decodedData.length - SALT_SIZE - IV_SIZE];

        System.arraycopy(decodedData, 0, salt, 0, SALT_SIZE);
        System.arraycopy(decodedData, SALT_SIZE, iv, 0, IV_SIZE);
        System.arraycopy(decodedData, SALT_SIZE + IV_SIZE, encryptedText, 0, encryptedText.length);

        SecretKey key = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        byte[] decryptedData = cipher.doFinal(encryptedText);

        return (decryptedData);
    }

    public static String decrypt(String encryptedText,char []password)throws Exception{
        return new String(decrypt(encryptedText.getBytes(),password));
    }

    private static SecretKey deriveKeyFromPassword(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] PBKDF2(char[] password,byte[]salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create PBEKeySpec with the given password, salt, iteration count, and key length
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        // Get an instance of the PBKDF2WithHmacSHA256 algorithm
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        // Generate the key
        return keyFactory.generateSecret(spec).getEncoded();
    }
}