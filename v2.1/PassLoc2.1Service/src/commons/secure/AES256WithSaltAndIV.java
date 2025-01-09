package commons.secure;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;


public class AES256WithSaltAndIV {

    private static final int SALT_LENGTH = 16;  // Salt length in bytes
    private static final int IV_LENGTH = 16;    // IV length in bytes
    private static final int KEY_LENGTH = 256;  // Key length in bits
    private static final int ITERATIONS = 65536;  // Iterations for key derivation
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // AES mode




    // Generate a random salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    // Derive AES key from the password and salt using PBKDF2
    private static SecretKey deriveKeyFromPassword(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    // Encrypt plaintext using AES-256 with a random IV
    public static byte[] encrypt(String plaintext, char[] password) throws Exception {
        // Generate random salt
        return encrypt(plaintext.getBytes(StandardCharsets.UTF_8), password);
    }

    public static byte[] encrypt(byte[] data,char[] password)throws Exception{
        // Generate random salt
        byte[] salt = generateSalt();

        // Derive key from password and salt
        SecretKey secretKey = deriveKeyFromPassword(password, salt);

        // Generate random IV
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Initialize cipher for encryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        // Encrypt the plaintext
        byte[] encrypted = cipher.doFinal(data);

        // Combine salt, IV, and ciphertext
        byte[] ivAndCiphertext = new byte[salt.length + iv.length + encrypted.length];
        System.arraycopy(salt, 0, ivAndCiphertext, 0, salt.length);  // Prepend salt
        System.arraycopy(iv, 0, ivAndCiphertext, salt.length, iv.length);  // Append IV
        System.arraycopy(encrypted, 0, ivAndCiphertext, salt.length + iv.length, encrypted.length);  // Append ciphertext

        // Return Base64-encoded encrypted data
        return ivAndCiphertext;
    }

    // Decrypt ciphertext using AES-256 with a stored IV and salt
    public static byte[] decrypt(byte[] encryptedData, char[] password) throws Exception {

        // Extract salt
        byte[] salt = Arrays.copyOfRange(encryptedData, 0, SALT_LENGTH);

        // Derive key from password and salt
        SecretKey secretKey = deriveKeyFromPassword(password, salt);

        // Extract IV
        byte[] iv = Arrays.copyOfRange(encryptedData, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract ciphertext
        byte[] ciphertext = Arrays.copyOfRange(encryptedData, SALT_LENGTH + IV_LENGTH, encryptedData.length);

        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        // Decrypt the ciphertext
        return cipher.doFinal(ciphertext);


    }



}
