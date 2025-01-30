package com.company;

import javax.swing.*;

public class GameWordle extends Game {
    private String correctWord;
    private int attemptsUsed;

    public GameWordle(int maxAttempts) {
        super(maxAttempts);
        this.correctWord = "APPLE";
        this.attemptsUsed = 0;
    }


    @Override
    public void startGame() {
        boolean hasWon = false;
        while (attemptsUsed < maxAttempts) {
            String guessedWord = JOptionPane.showInputDialog(null,
                    "Zgadnij 5-literowe słowo (Próba " + (attemptsUsed + 1) + " z " + maxAttempts + "):");
            if (guessedWord == null) {
                break;
            }

            guessedWord = guessedWord.toUpperCase();

            if (guessedWord.length() != 5) {
                JOptionPane.showMessageDialog(null, "Proszę wpisać dokładnie 5-literowe słowo.");
                continue;
            }

            attemptsUsed++;
            String feedback = WordChecker.checkWord(guessedWord, correctWord);

            JOptionPane.showMessageDialog(null, "Rezultat: " + feedback);

            if (guessedWord.equals(correctWord)) {
                JOptionPane.showMessageDialog(null, "Gratulacje! Zgadłeś słowo: " + correctWord);
                hasWon = true;
                break;
            }
        }

        if (!hasWon) {
            JOptionPane.showMessageDialog(null, "Koniec gry! Nie udało się zgadnąć słowa: " + correctWord);
        }
    }
}
