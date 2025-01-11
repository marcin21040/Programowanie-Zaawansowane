package com.company;

import javax.swing.*;

import javax.swing.*;
import java.awt.*;

public class MZ_GRA extends JFrame {

    public MZ_GRA() {
        setTitle("STRZELANKA");

        setSize(600, 800);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("GRA", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        add(welcomeLabel, BorderLayout.CENTER);
        JButton backButton = new JButton("POWRÓT DO MENU");
        backButton.addActionListener(e -> dispose());
        add(backButton, BorderLayout.SOUTH);
    }

    public void startGame() {
        setVisible(true);
    }
}
