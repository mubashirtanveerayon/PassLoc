import encryption.SecureEncryption;

public class Test {

    public static void main(String[] args) {
        SecureEncryption encr = new SecureEncryption(12L);
        String text = "hello##\t##nigga";
        System.out.println(text);
        String encrypted = encr.encrypt(text);
        System.out.println(encrypted);
        SecureEncryption decr = new SecureEncryption(12L);
        String decrypted = decr.decrypt(encrypted);
        System.out.println(decrypted);
        System.out.println((int)("\t".charAt(0)));
    }
}
