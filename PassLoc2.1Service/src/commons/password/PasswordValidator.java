package commons.password;

import commons.utils.exception.InvalidPasswordException;

public class PasswordValidator {


    public static final int PASSWORD_MINIMUM_LENGTH = 6;
    public static final int MINIMUM_DIGITS_REQUIRED = 4;

    public static void validate(String password){
        if(password.length()<PASSWORD_MINIMUM_LENGTH) throw new InvalidPasswordException();
        int digitsCount = 0;
        for(int i=0;i<password.length();i++) {
            if (Character.isDigit(password.charAt(i))) digitsCount++;
            if (digitsCount >= MINIMUM_DIGITS_REQUIRED) return;
        }
        throw new InvalidPasswordException();

    }


}
