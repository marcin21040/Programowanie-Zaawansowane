package com.company;

import javax.swing.*;
import java.awt.*;

public class WordleGameLogic {
    private final WordleGameUI gameUI;
    private final GridPanel gridPanel;
    private final KeyboardPanel keyboardPanel;
    private String currentGuess = "";
    private int currentRow = 0;
    private String correctWord;

    public WordleGameLogic(WordleGameUI gameUI) {
        this.gameUI = gameUI;
        this.gridPanel = gameUI.getGridPanel();
        this.keyboardPanel = gameUI.getKeyboardPanel();

        try {
            correctWord = DatamuseAPIClient.getRandomWord();
        } catch (Exception e) {
            e.printStackTrace();
            correctWord = "APPLE";
        }
    }

    public void handleInput(String input) {
        if (input.equals("ENTER")) {
            processGuess();
        } else if (input.equals("DELETE")) {
            deleteLastLetter();
        } else {
            addLetter(input);
        }
    }

    private void addLetter(String letter) {
        if (currentGuess.length() < 5) {
            gridPanel.setLetter(currentRow, currentGuess.length(), letter);
            currentGuess += letter;
        }
    }

    private void deleteLastLetter() {
        if (currentGuess.length() > 0) {
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
            gridPanel.clearCell(currentRow, currentGuess.length());
        }
    }

    private void processGuess() {
        if (currentGuess.length() == 5) {
            try {
                if (DatamuseAPIClient.isValidWord(currentGuess)) {
                    checkWord();
                    currentGuess = "";
                    currentRow++;
                } else {
                    JOptionPane.showMessageDialog(gameUI, "To słowo nie istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gameUI, "Błąd połączenia z API!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkWord() {
        boolean isCorrect = true;

        for (int i = 0; i < 5; i++) {
            char guessedLetter = currentGuess.charAt(i);
            char correctLetter = correctWord.charAt(i);
            String letterStr = String.valueOf(guessedLetter).toUpperCase();

            if (guessedLetter == correctLetter) {
                gridPanel.setCellColor(currentRow, i, Color.GREEN);
                keyboardPanel.setKeyButtonColor(letterStr, Color.GREEN);
            } else if (correctWord.contains(letterStr)) {
                gridPanel.setCellColor(currentRow, i, Color.YELLOW);
                keyboardPanel.setKeyButtonColor(letterStr, Color.YELLOW);
                isCorrect = false;
            } else {
                gridPanel.setCellColor(currentRow, i, Color.GRAY);
                keyboardPanel.setKeyButtonColor(letterStr, Color.GRAY);
                isCorrect = false;
            }
        }

        if (isCorrect) {
            JOptionPane.showMessageDialog(gameUI, "Gratulacje! Zgadłeś słowo!", "Wygrana", JOptionPane.INFORMATION_MESSAGE);
        } else if (currentRow == 5) {
            JOptionPane.showMessageDialog(gameUI, "Przegrałeś! Prawidłowe słowo to: " + correctWord, "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
