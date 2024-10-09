package commons.services.generator.password;

public class PasswordStrengthEvaluator {


    public static int evaluatePasswordStrength(String password) {
        int score = 0;

        if (password == null || password.isEmpty()) {
            return score;
        }

        // Criteria 1: Password length
        int length = password.length();
        if (length >=30) {
            score += 70;
        }else if(length >= 20) {
            score += 60;
        }else if (length > 12) {
            score += 50;
        }else {
            score -= 10;
        }

        // Criteria 2: Character diversity
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        if (hasLower) score += 5;
        if (hasUpper) score += 8;
        if (hasDigit) score += 10;
        if (hasSpecial) score += 15;

        // Criteria 3: Repetition (Penalize if repeated characters or sequences)
        score -= checkRepetitions(password);


        // Ensure the score is between 1 and 100
        if (score < 1) score = 1;
        if (score > 100) score = 100;

        return score;
    }

    // Method to check for repeated characters
    private static int checkRepetitions(String password) {
        int penalty = 0;
        for (int i = 0; i < password.length() - 2; i++) {
            String sub = password.substring(i, i + 3);
            if (password.indexOf(sub, i + 1) != -1) {
                penalty += 3; // Penalize repeated sequences
            }
        }
        return penalty;
    }


}

