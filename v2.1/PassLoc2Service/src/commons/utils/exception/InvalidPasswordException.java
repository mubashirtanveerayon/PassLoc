package commons.utils.exception;

import commons.services.generator.password.PasswordValidator;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException() {
        super("Password must have a length of " + PasswordValidator.PASSWORD_MINIMUM_LENGTH + " characters including at least 4 digits");
    }
}