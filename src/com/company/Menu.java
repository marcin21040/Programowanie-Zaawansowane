package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame implements ActionListener {
    private JButton newGameButton_Michala;
    private JButton newGameButton_Mateusza;
    private JButton newGameButton_Marcina;
    private JButton exitButton;

    public Menu() {

        setTitle("Menu Gry");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));


        newGameButton_Michala = new JButton("Gra Michała");
        newGameButton_Mateusza = new JButton("Gra Mateusza");
        newGameButton_Marcina = new JButton("Gra Marcina");
        exitButton = new JButton("Wyjdź");


        newGameButton_Michala.addActionListener(this);
        newGameButton_Mateusza.addActionListener(this);
        newGameButton_Marcina.addActionListener(this);
        exitButton.addActionListener(this);


        add(newGameButton_Michala);
        add(newGameButton_Mateusza);
        add(newGameButton_Marcina);
        add(exitButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton_Michala) {
            GameMichalStart gameMichal = new GameMichalStart();
            gameMichal.startGame();
            JOptionPane.showMessageDialog(this, "Nowa gra rozpoczęta!");
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == newGameButton_Mateusza) {
            MZ_GRA gameMateusz = new MZ_GRA();
            gameMateusz.rozpocznijGre();
            JOptionPane.showMessageDialog(this, "Nowa gra rozpoczęta!");
        } else if (e.getSource() == newGameButton_Marcina) {
            WordleGameUI gameMarcin = new WordleGameUI();
            gameMarcin.setVisible(true);  // Otwiera okno z grą Wordle
            JOptionPane.showMessageDialog(this, "Nowa gra Marcina rozpoczęta!");
        }
    }

}
