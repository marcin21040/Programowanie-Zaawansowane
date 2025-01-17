package com.company;

public class WordChecker {

    public static String checkWord(String guessedWord, String correctWord) {
        char[] result = new char[guessedWord.length()];
        boolean[] correctFlags = new boolean[correctWord.length()];

        // Sprawdź litery na właściwej pozycji
        for (int i = 0; i < guessedWord.length(); i++) {
            if (guessedWord.charAt(i) == correctWord.charAt(i)) {
                result[i] = '*'; // Trafienie
                correctFlags[i] = true;
            } else {
                result[i] = '_'; // Domyślnie brak trafienia
            }
        }

        // Sprawdź litery, które są w słowie, ale na złej pozycji
        for (int i = 0; i < guessedWord.length(); i++) {
            if (result[i] == '*') continue; // Pomiń poprawnie zgadnięte

            for (int j = 0; j < correctWord.length(); j++) {
                if (guessedWord.charAt(i) == correctWord.charAt(j) && !correctFlags[j]) {
                    result[i] = '+'; // Litera istnieje, ale w złej pozycji
                    correctFlags[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }
}
