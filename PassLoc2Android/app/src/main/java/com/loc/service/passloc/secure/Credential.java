package com.loc.service.passloc.secure;


import com.loc.service.utils.HelperFunctions;
import com.loc.service.utils.PasswordValidator;
import com.loc.service.utils.exception.CredentialAlreadyExistsException;
import com.loc.service.utils.exception.InvalidPasswordException;

//import java.util.Random;

public class Credential {

    private static Credential credential;

//    private byte[] randArray;
//    private int randArrayIndex=0;
//
    private final char pinPaddingCharacter = '~';

    private final String hashedMasterPassword;



    public String getHashedMasterPassword() {
        return hashedMasterPassword;
    }

    private Credential (String masterPassword) throws InvalidPasswordException {
        PasswordValidator.validate(masterPassword);
        hashedMasterPassword = HelperFunctions.sha256(masterPassword);
//        int hashCode = hashedMasterPassword.hashCode();
//        Random rng = new Random(hashCode);
//        randArray = new byte[256];
//        rng.nextBytes(randArray);
//
//        pinPaddingCharacter = (char)( rng.nextInt(26)+65);


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
//        if(credential != null)
//            credential.nullify();
        credential = null;
    }


//    public void nullify(){
//        for(int i=0;i<randArray.length;i++)
//            randArray[i] = 0;
//        randArray = null;
//        pinPaddingCharacter = ' ';
//        hashedMasterPassword = "";
//        randArrayIndex = 0;
//    }

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
        StringBuilder key = new StringBuilder(text);
        for(char c:hashedMasterPassword.toCharArray())
            if(Character.isDigit(c))
                key.append(c);

        key.append(hashedMasterPassword.substring(text.length() % hashedMasterPassword.length()));

        return HelperFunctions.sha256(key.toString());
    }


}