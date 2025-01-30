package com.company;

public class WordChecker {

    public static String checkWord(String guessedWord, String correctWord) {
        char[] result = new char[guessedWord.length()];
        boolean[] correctFlags = new boolean[correctWord.length()];

        for (int i = 0; i < guessedWord.length(); i++) {
            if (guessedWord.charAt(i) == correctWord.charAt(i)) {
                result[i] = '*';
                correctFlags[i] = true;
            } else {
                result[i] = '_';
            }
        }
        for (int i = 0; i < guessedWord.length(); i++) {
            if (result[i] == '*') continue;

            for (int j = 0; j < correctWord.length(); j++) {
                if (guessedWord.charAt(i) == correctWord.charAt(j) && !correctFlags[j]) {
                    result[i] = '+';
                    correctFlags[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }
}
