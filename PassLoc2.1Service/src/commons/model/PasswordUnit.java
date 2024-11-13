package commons.model;

import commons.utils.HelperFunctions;
import commons.utils.exception.NoAvailableCharacterException;

import java.util.Random;

public class PasswordUnit {

    private int remaining;
    private PasswordElements.CharacterType type;

    private String available;
    private final boolean repeatCharacter;

    public PasswordUnit(PasswordElements.CharacterType type,int count,boolean repetitionAllowed){
        this.type = type;
        remaining=count;

        if(type == PasswordElements.CharacterType.DIGIT)available = PasswordElements.DIGITS;
        else if(type == PasswordElements.CharacterType.UPPERCASE_LETTER)available = PasswordElements.UPPERCASE_LETTER;
        else if(type == PasswordElements.CharacterType.LOWERCASE_LETTER)available = PasswordElements.LOWERCASE_LETTER;
        else available = PasswordElements.SPECIAL;


        repeatCharacter = repetitionAllowed;



    }

    public PasswordElements.CharacterType getType(){
        return type;
    }

    public int getRemaining(){
        return remaining;
    }

    public char getRandom(){

        if( isUnavailable())throw new NoAvailableCharacterException();

        Random rng = new Random();

        int randomIndex = rng.nextInt(available.length());

        char randomCharacter = available.charAt(randomIndex);
        if(!repeatCharacter )available = HelperFunctions.removeChar(available,randomCharacter);
        remaining --;
        return randomCharacter;

    }

    public boolean isUnavailable(){
        return getRemaining() <= 0 || available.isEmpty();
    }

}
