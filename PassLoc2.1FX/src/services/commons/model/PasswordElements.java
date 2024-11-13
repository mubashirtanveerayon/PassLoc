package services.commons.model;

public class PasswordElements {

    public static final String VOWELS_LOWER = "aeiou";
    public static final String VOWELS_UPPER = "AEIOU";

    public static final String CONSONANTS_LOWER = "bcdfghjklmnpqrstvwxyz";
    public static final String CONSONANTS_UPPER = "BCDFGHJKLMNPQRSTVWXYZ";

    public static final String UPPERCASE_LETTER = CONSONANTS_UPPER + VOWELS_UPPER + VOWELS_UPPER;
    public static final String LOWERCASE_LETTER = CONSONANTS_LOWER + VOWELS_LOWER + VOWELS_LOWER;

    public static final String DIGITS = "0123456789";
    public static final String SPECIAL = "~!@#$%^&*()_+-=[]\\{}|;':\"<>?,./";

    public enum CharacterType{

        UPPERCASE_LETTER,LOWERCASE_LETTER,DIGIT,SPECIAL

    };
}
