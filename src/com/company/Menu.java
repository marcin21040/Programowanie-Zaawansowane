package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame implements ActionListener {
    private final JButton newGameButton_Michala;
    private final JButton newGameButton_Mateusza;
    private final JButton newGameButton_Marcina;
    private final JButton exitButton;

    public Menu() {
        setTitle("Menu Gry");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 240, 240)); // Tło menu
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 20, 20)); // Więcej odstępów między przyciskami
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300)); // Dodanie marginesów


        newGameButton_Michala = createStyledButton("Gra Michała");
        newGameButton_Mateusza = createStyledButton("Gra Mateusza");
        newGameButton_Marcina = createStyledButton("Gra Marcina");
        exitButton = createStyledButton("Wyjdź");


        newGameButton_Michala.addActionListener(this);
        newGameButton_Mateusza.addActionListener(this);
        newGameButton_Marcina.addActionListener(this);
        exitButton.addActionListener(this);


        buttonPanel.add(newGameButton_Michala);
        buttonPanel.add(newGameButton_Mateusza);
        buttonPanel.add(newGameButton_Marcina);
        buttonPanel.add(exitButton);


        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 80));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton_Michala) {
            GameMichalStart gameMichal = new GameMichalStart();
            gameMichal.startGame();
            JOptionPane.showMessageDialog(this, "Nowa gra Michała rozpoczęta!");
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == newGameButton_Mateusza) {
            MZ_GRA gameMateusz = new MZ_GRA();
            gameMateusz.rozpocznijGre();
            JOptionPane.showMessageDialog(this, "Nowa gra Mateusza rozpoczęta!");
        } else if (e.getSource() == newGameButton_Marcina) {
            WordleGameUI gameMarcin = new WordleGameUI();
            gameMarcin.setVisible(true);
            JOptionPane.showMessageDialog(this, "Nowa gra Marcina rozpoczęta!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Menu menu = new Menu();
            menu.setVisible(true);
        });
    }
}
