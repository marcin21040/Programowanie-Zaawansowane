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
    private final Map<String, JButton> keyButtonMap;

    public WordleGameUI() {
        setTitle("Wordle");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


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


        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 9, 5, 5));

        String[] keys = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M"
        };

        keyboardButtons = new JButton[keys.length];
        keyButtonMap = new HashMap<>();

        for (int i = 0; i < keys.length; i++) {
            keyboardButtons[i] = new JButton(keys[i]);
            keyboardButtons[i].setPreferredSize(new Dimension(50, 50));
            keyboardButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
            keyboardButtons[i].setBackground(new Color(230, 230, 230));
            keyboardButtons[i].setForeground(Color.BLACK);
            keyboardButtons[i].setFocusPainted(false);
            keyboardButtons[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            keyboardButtons[i].addActionListener(this);
            keyboardPanel.add(keyboardButtons[i]);
            keyButtonMap.put(keys[i], keyboardButtons[i]);
        }


        enterButton = new JButton("ENTER");
        enterButton.setPreferredSize(new Dimension(100, 50));
        enterButton.setFont(new Font("Arial", Font.BOLD, 18));
        enterButton.setBackground(new Color(59, 89, 182));
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        enterButton.addActionListener(this);
        keyboardPanel.add(enterButton);


        deleteButton = new JButton("DELETE");
        deleteButton.setPreferredSize(new Dimension(100, 50));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18));
        deleteButton.setBackground(new Color(192, 57, 43)); // Czerwony kolor
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        deleteButton.addActionListener(this);
        keyboardPanel.add(deleteButton);

        mainPanel.add(keyboardPanel, BorderLayout.SOUTH);

        add(mainPanel);


        addKeyListener(this);
        setFocusable(true);

        try {
            correctWord = DatamuseAPIClient.getRandomWord();
        } catch (Exception e) {
            e.printStackTrace();
            correctWord = "APPLE";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        if (clickedButton == enterButton) {
            if (currentGuess.length() == 5) {
                try {
                    if (DatamuseAPIClient.isValidWord(currentGuess)) {
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
            keyChar = Character.toUpperCase(keyChar);
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
    }


    private void checkWord(String guess) {
        boolean isCorrect = true;

        for (int i = 0; i < 5; i++) {
            char guessedLetter = guess.charAt(i);
            char correctLetter = correctWord.charAt(i);

            JButton correspondingButton = keyButtonMap.get(String.valueOf(guessedLetter).toUpperCase());

            if (guessedLetter == correctLetter) {
                grid[currentRow][i].setBackground(Color.GREEN);
                if (correspondingButton != null) {
                    correspondingButton.setBackground(Color.GREEN);
                }
            } else if (correctWord.contains(String.valueOf(guessedLetter))) {
                grid[currentRow][i].setBackground(Color.YELLOW);
                if (correspondingButton != null) {
                    correspondingButton.setBackground(Color.YELLOW);
                }
                isCorrect = false;
            } else {
                grid[currentRow][i].setBackground(Color.GRAY);
                if (correspondingButton != null) {
                    correspondingButton.setBackground(Color.GRAY);
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
