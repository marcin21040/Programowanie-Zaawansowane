package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordleGameUI extends JFrame implements ActionListener {
    private final JTextField[][] grid;
    private final JButton[] keyboardButtons;
    private final JButton enterButton, deleteButton;
    private String currentGuess = "";
    private int currentRow = 0;
    private String correctWord = ""; // Wylosowane słowo

    public WordleGameUI() {
        setTitle("Wordle");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Losowanie słowa o 5 literach z API Datamuse
        try {
            correctWord = DatamuseAPIClient.getRandomWord();
        } catch (Exception e) {
            e.printStackTrace();
            correctWord = "CRANE"; // Fallback, jeśli coś pójdzie nie tak
        }

        System.out.println("Wylosowane słowo: " + correctWord); // Dla celów testowych, wypisz wylosowane słowo

        // Tworzymy panel planszy
        JPanel gridPanel = new JPanel(new GridLayout(6, 5, 5, 5));
        grid = new JTextField[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = new JTextField();
                grid[i][j].setEditable(false);
                grid[i][j].setHorizontalAlignment(JTextField.CENTER);
                grid[i][j].setFont(new Font("Arial", Font.BOLD, 24));
                grid[i][j].setBackground(Color.WHITE);
                gridPanel.add(grid[i][j]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        // Tworzymy panel klawiatury
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 9, 5, 5)); // Dopasowanie układu klawiatury

        String[] keys = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M"
        };

        keyboardButtons = new JButton[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keyboardButtons[i] = new JButton(keys[i]); // Ustawiamy literę na przycisku
            keyboardButtons[i].setPreferredSize(new Dimension(50, 50)); // Rozmiar przycisków
            keyboardButtons[i].setFont(new Font("Arial", Font.BOLD, 18)); // Rozmiar czcionki
            keyboardButtons[i].setMargin(new Insets(0, 0, 0, 0)); // Ustawienie marginesu na 0
            keyboardButtons[i].addActionListener(this);
            keyboardPanel.add(keyboardButtons[i]);
        }

        // Przycisk "Enter"
        enterButton = new JButton("ENTER");
        enterButton.setPreferredSize(new Dimension(100, 50)); // Większy przycisk
        enterButton.setFont(new Font("Arial", Font.BOLD, 18)); // Rozmiar czcionki
        enterButton.setMargin(new Insets(0, 0, 0, 0)); // Ustawienie marginesu na 0
        enterButton.addActionListener(this);
        keyboardPanel.add(enterButton);

        // Przycisk "Delete"
        deleteButton = new JButton("DELETE");
        deleteButton.setPreferredSize(new Dimension(100, 50)); // Większy przycisk
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18)); // Rozmiar czcionki
        deleteButton.setMargin(new Insets(0, 0, 0, 0)); // Ustawienie marginesu na 0
        deleteButton.addActionListener(this);
        keyboardPanel.add(deleteButton);

        add(keyboardPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        if (clickedButton == enterButton) {
            if (currentGuess.length() == 5) {
                // Sprawdzamy hasło
                checkWord(currentGuess);
                currentGuess = "";
                currentRow++;
            }
        } else if (clickedButton == deleteButton) {
            if (currentGuess.length() > 0) {
                currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
                grid[currentRow][currentGuess.length()].setText("");
            }
        } else {
            if (currentGuess.length() < 5) {
                String letter = clickedButton.getText();
                grid[currentRow][currentGuess.length()].setText(letter);
                currentGuess += letter;
            }
        }
    }

    private void checkWord(String guess) {
        for (int i = 0; i < 5; i++) {
            char guessedLetter = guess.charAt(i);
            char correctLetter = correctWord.charAt(i);

            if (guessedLetter == correctLetter) {
                grid[currentRow][i].setBackground(Color.GREEN); // Zgadnięta litera na właściwym miejscu
            } else if (correctWord.contains(String.valueOf(guessedLetter))) {
                grid[currentRow][i].setBackground(Color.YELLOW); // Litera w słowie, ale na złym miejscu
            } else {
                grid[currentRow][i].setBackground(Color.GRAY); // Litery nie ma w słowie
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordleGameUI gameUI = new WordleGameUI();
            gameUI.setVisible(true);
        });
    }
}
