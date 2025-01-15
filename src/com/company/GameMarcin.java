package com.company;

import javax.swing.*;
import java.util.Random;

public class GameMarcin extends Game {
    private int numberToGuess;
    private int attemptsUsed;

    public GameMarcin(int maxAttempts) {
        super(maxAttempts);
        this.numberToGuess = new Random().nextInt(100) + 1; // Losowanie liczby od 1 do 100
        this.attemptsUsed = 0;
    }

    @Override
    public void startGame() {
        boolean hasWon = false;
        while (attemptsUsed < maxAttempts) {
            String input = JOptionPane.showInputDialog(null,
                    "Zgadnij liczbę od 1 do 100 (Próba " + (attemptsUsed + 1) + " z " + maxAttempts + "):");
            if (input == null) {
                break; // Gracz anulował
            }

            try {
                int guess = Integer.parseInt(input);
                attemptsUsed++;

                if (guess == numberToGuess) {
                    JOptionPane.showMessageDialog(null, "Gratulacje! Zgadłeś liczbę: " + numberToGuess);
                    hasWon = true;
                    break;
                } else if (guess < numberToGuess) {
                    JOptionPane.showMessageDialog(null, "Za mała liczba! Spróbuj ponownie.");
                } else {
                    JOptionPane.showMessageDialog(null, "Za duża liczba! Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Proszę wpisać prawidłową liczbę.");
            }
        }

        if (!hasWon) {
            JOptionPane.showMessageDialog(null, "Koniec gry! Nie udało się zgadnąć liczby: " + numberToGuess);
        }
    }
}
