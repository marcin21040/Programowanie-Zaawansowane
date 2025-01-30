package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordleGameUI extends JFrame implements ActionListener {
    private final GridPanel gridPanel;
    private final KeyboardPanel keyboardPanel;
    private final WordleGameLogic gameLogic;

    public WordleGameUI() {
        setTitle("Wordle");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gridPanel = new GridPanel(); // Najpierw inicjalizujemy GridPanel
        keyboardPanel = new KeyboardPanel(this); // Potem KeyboardPanel
        gameLogic = new WordleGameLogic(this); // Dopiero teraz przekazujemy this

        add(gridPanel, BorderLayout.CENTER);
        add(keyboardPanel, BorderLayout.SOUTH);
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public KeyboardPanel getKeyboardPanel() {
        return keyboardPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        gameLogic.handleInput(clickedButton.getText());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordleGameUI wordleGame = new WordleGameUI();
            wordleGame.setVisible(true);
        });
    }
}
