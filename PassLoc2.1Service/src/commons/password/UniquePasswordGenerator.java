package commons.password;


import commons.model.PasswordUnit;
import commons.utils.HelperFunctions;

import java.util.List;
import java.util.Random;

public class UniquePasswordGenerator {

    public static String generateStringFromPasswordUnits(List<PasswordUnit> units){

        String generatedChars = "";



        for(PasswordUnit unit:units){
            while(!unit.isUnavailable())generatedChars += unit.getRandom();
        }

        return generatedChars;

    }

    public static String generateStringFromPasswordUnits(PasswordUnit[] units){

        String generatedChars = "";



        for(PasswordUnit unit:units){
            while(!unit.isUnavailable())generatedChars += unit.getRandom();
        }

        return generatedChars;

    }

    public static String shuffle(String s){
        StringBuilder sb = new StringBuilder();
        Random rng = new Random();
        while(!s.isEmpty()){
            char random = s.charAt(rng.nextInt(s.length()));
            s = HelperFunctions.removeChar(s,random);
            sb.append(random);
        }

        return sb.toString();
    }

}
