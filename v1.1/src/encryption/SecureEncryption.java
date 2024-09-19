package encryption;



import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

/**
 *
 * @author ayon
 */
public class SecureEncryption {


    private String shiftKey="";
    private String hashModifierKey="";

    private int[] encryptionKey;


    private static final int MAX_KEY_LENGTH=32;
    private static final int MIN_KEY_LENGTH=10;

    private long seed;

    private Random rng;




    public SecureEncryption(long seed,String shiftKey,String hashModifierKey){
        this.seed=seed;
        this.rng=new Random(seed);
        this.shiftKey=shiftKey;
        this.hashModifierKey=hashModifierKey;
        configureKey();
    }






    public SecureEncryption(long seed){
        this.seed=seed;
        this.rng=new Random(seed);
        int keyLength=rng.nextInt(MIN_KEY_LENGTH,MAX_KEY_LENGTH)+1;


        for(int i=0;i<keyLength;i++){


            shiftKey += (char)(rng.nextInt(95)+32);


            int hashType = rng.nextInt(3);

            if (hashType == 1){
                hashModifierKey += (char)(rng.nextInt(1,10)+48);
            }else{


                hashModifierKey+=(char)(hashType+97);
            }


        }

        configureKey();
    }


    public static String md5Hash(String password) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md=null;
        try {
            md = MessageDigest.getInstance("SHA-256");

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        md.update(password.getBytes());
        byte[] byteArray = md.digest();
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public SecureEncryption(){
        rng=new Random();
        int keyLength=rng.nextInt(MIN_KEY_LENGTH,MAX_KEY_LENGTH)+1;


        for(int i=0;i<keyLength;i++){


            shiftKey += (char)(rng.nextInt(95)+32);


            int hashType = rng.nextInt(3);

            if (hashType == 1){
                hashModifierKey += (char)(rng.nextInt(1,10)+48);
            }else{


                hashModifierKey+=(char)(hashType+97);
            }


        }

        configureKey();

    }


    private void configureKey(){
        encryptionKey=new int[shiftKey.length()];


        for(int i=0;i<encryptionKey.length;i++){

            int shift = ((int)(shiftKey.charAt(i))-32);

            char hashType = hashModifierKey.charAt(i);
            if(Character.isDigit(hashType)){

            }else{

                encryptionKey[i] = shift * (hashType=='a'?1:-1);
            }


        }



    }

    public String encrypt(String text){

        String result="";

        int keyIndex=0;



        for(int i=0;i<text.length();i++){
            int ascii = (int)text.charAt(i);
            if(Character.isDigit(hashModifierKey.charAt(keyIndex))){

                int gibLength=(int)(hashModifierKey.charAt(keyIndex))-48;
                for(int gibIndex=0;gibIndex<gibLength;gibIndex++){
                    result += (char)rng.nextInt(32,127);
                }
                i--;

            }else if(ascii>=32 && ascii <=126){
                int mappedChar = (int)(text.charAt(i))-32;

                int remappedChar = mappedChar + encryptionKey[keyIndex];
                if (remappedChar < 0){
                    remappedChar = 95 + remappedChar;
                }

                char encryptedChar = (char)(remappedChar % 95+32);
                result += encryptedChar;

            }else result += text.charAt(i);
            keyIndex++;
            if(keyIndex >= encryptionKey.length){
                keyIndex=0;
            }





        }




        return result;
    }



    public String decrypt(String text){

        String result = "";

        int keyIndex = 0;

        for(int i=0;i<text.length();i++){
            char hash = hashModifierKey.charAt(keyIndex);
            int ascii = (int)text.charAt(i);
            if (Character.isDigit(hash)){
                i = i + ((int)hash)-48 - 1;
            }else if(ascii>=32 && ascii <=126){
                int mappedChar = (int)(text.charAt(i))-32;

                int remappedChar = mappedChar - encryptionKey[keyIndex];
                if (remappedChar < 0){
                    remappedChar = 95 + remappedChar;
                }

                char decryptedChar = (char)(remappedChar % 95+32);
                result += decryptedChar;
            }else result += text.charAt(i);

            keyIndex++;
            if(keyIndex >= encryptionKey.length){
                keyIndex=0;
            }

        }




        return result;
    }

    public String encode(String text){
        return base64Encode(encrypt(text));
    }

    public String decode(String text){
        return decrypt(base64Decode(text));
    }

    public static String base64Encode(String text){
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public static String base64Decode(String text){
        return new String(Base64.getDecoder().decode(text));
    }


    public long getSeed() {
        return seed;
    }
}

