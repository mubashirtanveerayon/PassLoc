package services.secure;


import utils.HelperFunctions;
import services.generator.password.PasswordValidator;
import utils.exception.CredentialAlreadyExistsException;
import utils.exception.InvalidPasswordException;

//import java.util.Random;

public class Credential {

    private static Credential credential;

//    private byte[] randArray;
//    private int randArrayIndex=0;
//
//    private final char pinPaddingCharacter = '~';

    private final char[] hashedMasterPassword,masterPassword;

    private AES256WithPassword encryptor;

    boolean initialized = false;

    private Credential (String masterPassword) throws InvalidPasswordException {
        PasswordValidator.validate(masterPassword);
        hashedMasterPassword = HelperFunctions.sha256(masterPassword).toCharArray();
        this.masterPassword = masterPassword.toCharArray();

//        int hashCode = hashedMasterPassword.hashCode();
//        Random rng = new Random(hashCode);
//        randArray = new byte[256];
//        rng.nextBytes(randArray);
//
//        pinPaddingCharacter = (char)( rng.nextInt(26)+65);


    }


    public void initializeEncryptor(String password){
        if(initialized)
            throw new CredentialAlreadyExistsException("Attempt to re-initialize AES256 encryptor");

        encryptor = new AES256WithPassword(derivePasswordKey(masterPassword,password));
        initialized = true;
    }

    public String encrypt(String text){
        return encryptor.encrypt(text);
    }

    public String decrypt(String text){
        return encryptor.decrypt(text);
    }


    public static Credential getInstance(String newMasterPassword) throws CredentialAlreadyExistsException{
        if(credential == null){
            credential = new Credential(newMasterPassword);
            return credential;
        }
        throw new CredentialAlreadyExistsException();
    }

    public static Credential getInstance(){
        return credential;
    }


//    public static void resetInstance(String oldMasterPassword,String newMasterPassword) throws CredentialAuthenticationFailedException{
//        if(credential == null || HelperFunctions.sha256(oldMasterPassword).equals(credential.hashedMasterPassword)) {
//            credential = new Credential(newMasterPassword);
//        }else throw new CredentialAuthenticationFailedException();
//    }

    public static void resetInstance(){
        if(credential != null)
            credential.nullify();


        credential = null;
    }


    public void resetEncryptor(){
        encryptor = null;
        initialized=false;
    }

    public void nullify(){

        for(int i=0;i<hashedMasterPassword.length;i++)
            hashedMasterPassword[i] = 0;
        for(int i=0;i<masterPassword.length;i++)
            masterPassword[i] = 0;
        encryptor = null;

    }

//    public String derivePasswordKey(String password) throws InvalidPasswordException{
//
//        PasswordValidator.validate(password);
//
//        StringBuilder pin = new StringBuilder();
//        char[] passwordArray = password.toCharArray();
//        for(char c:passwordArray)
//            if(Character.isDigit(c))
//                pin.append(c);
//            else
//                pin.append(pinPaddingCharacter);
//
//
//        byte[] pinBytes = pin.toString().getBytes();
//
//
//        StringBuilder key = new StringBuilder();
//
//        int pinIndex = 0;
//
//
//
//        for(int i=0;i<passwordArray.length;i++){
//
//
//
//            key.append(passwordArray[i]);
//
//            byte randomByte = randArray[randArrayIndex];
//            if(randomByte>0 || HelperFunctions.primeByte(randomByte))
//                key.append(pinBytes[pinIndex]*randomByte);
//
//
//
//
//            randArrayIndex++;
//            if(randArrayIndex == randArray.length)randArrayIndex=0;
//
//            pinIndex++;
//            if(pinIndex == pinBytes.length)pinIndex=0;
//        }
//
//        return HelperFunctions.sha256( key.toString());
//
//    }

    public String derivePasswordKey(String text){
        return derivePasswordKey(hashedMasterPassword, text);
    }

    public static String derivePasswordKey(char[] passwordArray,String text){
        StringBuilder key = new StringBuilder(text);
        StringBuilder password = new StringBuilder();
        for(char c:passwordArray) {
            if (Character.isDigit(c))
                key.append(c);
            password.append(c);
        }


        key.append(password.substring(text.length() % passwordArray.length));



        return HelperFunctions.sha256(key.toString());
    }


}
