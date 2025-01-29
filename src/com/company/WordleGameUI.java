package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class WordleGameUI extends JFrame implements ActionListener, KeyListener {
    private final JTextField[][] grid;
    private final JButton[] keyboardButtons;
    private final JButton enterButton, deleteButton;
    private String currentGuess = "";
    private int currentRow = 0;
    private String correctWord; // Hasło do zgadnięcia pobierane z API
    private final Map<String, JButton> keyButtonMap; // Mapa liter do przycisków

    public WordleGameUI() {
        setTitle("Wordle");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Dodajemy margines do okna
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marginesy na około

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
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Ramki wokół liter
                gridPanel.add(grid[i][j]);
            }
        }
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Tworzymy panel klawiatury
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 9, 5, 5)); // Dopasowanie układu klawiatury

        String[] keys = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M"
        };

        keyboardButtons = new JButton[keys.length];
        keyButtonMap = new HashMap<>(); // Tworzymy mapę do przechowywania przycisków

        for (int i = 0; i < keys.length; i++) {
            keyboardButtons[i] = new JButton(keys[i]);
            keyboardButtons[i].setPreferredSize(new Dimension(50, 50));
            keyboardButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
            keyboardButtons[i].setBackground(new Color(230, 230, 230)); // Jasne tło przycisków
            keyboardButtons[i].setForeground(Color.BLACK); // Kolor tekstu
            keyboardButtons[i].setFocusPainted(false); // Usunięcie efektu kliknięcia
            keyboardButtons[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2)); // Ramki przycisków
            keyboardButtons[i].addActionListener(this);
            keyboardPanel.add(keyboardButtons[i]);
            keyButtonMap.put(keys[i], keyboardButtons[i]); // Dodajemy do mapy literę i przycisk
        }

        // Przycisk "Enter"
        enterButton = new JButton("ENTER");
        enterButton.setPreferredSize(new Dimension(100, 50));
        enterButton.setFont(new Font("Arial", Font.BOLD, 18));
        enterButton.setBackground(new Color(59, 89, 182)); // Ciemnoniebieski kolor
        enterButton.setForeground(Color.WHITE); // Biały tekst
        enterButton.setFocusPainted(false);
        enterButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        enterButton.addActionListener(this);
        keyboardPanel.add(enterButton);

        // Przycisk "Delete"
        deleteButton = new JButton("DELETE");
        deleteButton.setPreferredSize(new Dimension(100, 50));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18));
        deleteButton.setBackground(new Color(192, 57, 43)); // Czerwony kolor
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        deleteButton.addActionListener(this);
        keyboardPanel.add(deleteButton);

        // Dodajemy klawiaturę do głównego panelu
        mainPanel.add(keyboardPanel, BorderLayout.SOUTH);

        // Dodajemy główny panel do okna
        add(mainPanel);

        // Dodajemy nasłuchiwanie klawiszy z klawiatury
        addKeyListener(this);
        setFocusable(true); // Ustawienie okna na fokus, aby odbierało zdarzenia z klawiatury

        // Pobranie hasła z API na start gry
        try {
            correctWord = DatamuseAPIClient.getRandomWord();
        } catch (Exception e) {
            e.printStackTrace();
            correctWord = "CRANE"; // Fallback, jeśli coś pójdzie nie tak
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        if (clickedButton == enterButton) {
            if (currentGuess.length() == 5) {
                try {
                    // Sprawdzamy, czy słowo istnieje w API
                    if (DatamuseAPIClient.isValidWord(currentGuess)) {
                        // Sprawdzamy hasło
                        checkWord(currentGuess);
                        currentGuess = "";
                        currentRow++;
                    } else {
                        JOptionPane.showMessageDialog(this, "To słowo nie istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Błąd połączenia z API!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
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

    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (Character.isLetter(keyChar) && currentGuess.length() < 5) {
            keyChar = Character.toUpperCase(keyChar); // Upewnij się, że litery są wpisywane wielkimi literami
            grid[currentRow][currentGuess.length()].setText(String.valueOf(keyChar));
            currentGuess += keyChar;
        } else if (keyChar == KeyEvent.VK_BACK_SPACE && currentGuess.length() > 0) {
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
            grid[currentRow][currentGuess.length()].setText("");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && currentGuess.length() == 5) {
            checkWord(currentGuess);
            currentGuess = "";
            currentRow++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Pusta metoda (nie musimy jej obsługiwać, ale jest wymagana przez interfejs KeyListener)
    }

    // Zaktualizowana metoda checkWord, aby obsługiwać wygraną i przegraną
    private void checkWord(String guess) {
        boolean isCorrect = true; // Flaga do sprawdzenia, czy zgadłeś poprawnie

        for (int i = 0; i < 5; i++) {
            char guessedLetter = guess.charAt(i);
            char correctLetter = correctWord.charAt(i);

            JButton correspondingButton = keyButtonMap.get(String.valueOf(guessedLetter).toUpperCase());

            if (guessedLetter == correctLetter) {
                grid[currentRow][i].setBackground(Color.GREEN); // Zgadnięta litera na właściwym miejscu
                if (correspondingButton != null) {
                    correspondingButton.setBackground(Color.GREEN); // Zmieniamy kolor na zielony na przycisku klawiatury
                }
            } else if (correctWord.contains(String.valueOf(guessedLetter))) {
                grid[currentRow][i].setBackground(Color.YELLOW); // Zgadnięta litera w złym miejscu
                if (correspondingButton != null) {
                    correspondingButton.setBackground(Color.YELLOW); // Zmieniamy kolor na żółty
                }
                isCorrect = false;
            } else {
                grid[currentRow][i].setBackground(Color.GRAY); // Zła litera
                if (correspondingButton != null) {
                    correspondingButton.setBackground(Color.GRAY); // Zmieniamy kolor na szary na klawiaturze
                }
                isCorrect = false;
            }
        }

        if (isCorrect) {
            JOptionPane.showMessageDialog(this, "Gratulacje! Zgadłeś słowo!", "Wygrana", JOptionPane.INFORMATION_MESSAGE);
        } else if (currentRow == 5) {
            JOptionPane.showMessageDialog(this, "Przegrałeś! Prawidłowe słowo to: " + correctWord, "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordleGameUI wordleGame = new WordleGameUI();
            wordleGame.setVisible(true);
        });
    }
}
