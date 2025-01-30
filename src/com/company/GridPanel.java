package com.company;

import javax.swing.*;
import java.awt.*;

public class GridPanel extends JPanel {
    private final JTextField[][] grid;

    public GridPanel() {
        setLayout(new GridLayout(6, 5, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        grid = new JTextField[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = new JTextField();
                grid[i][j].setEditable(false);
                grid[i][j].setHorizontalAlignment(JTextField.CENTER);
                grid[i][j].setFont(new Font("Arial", Font.BOLD, 24));
                grid[i][j].setBackground(Color.WHITE);
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                add(grid[i][j]);
            }
        }
    }

    public void setLetter(int row, int col, String letter) {
        grid[row][col].setText(letter);
    }

    public void setCellColor(int row, int col, Color color) {
        grid[row][col].setBackground(color);
    }

    public void clearCell(int row, int col) {
        grid[row][col].setText("");  // Metoda usuwająca tekst z pola
    }
}
