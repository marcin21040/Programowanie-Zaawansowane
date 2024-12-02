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
        // Ustawienia ramki (okna)
        setTitle("Menu Gry");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1)); // Siatka 3x1, każda komórka na inny przycisk

        // Tworzenie przycisków
        newGameButton_Michala = new JButton("Gra Michała");
        newGameButton_Mateusza = new JButton("Gra Mateusza");
        newGameButton_Marcina = new JButton("Gra Marcina");
        exitButton = new JButton("Wyjdź");

        // Dodanie listenerów (nasłuchiwanie akcji na przyciskach)
        newGameButton_Michala.addActionListener(this);
        newGameButton_Mateusza.addActionListener(this);
        newGameButton_Marcina.addActionListener(this);
        exitButton.addActionListener(this);

        // Dodanie przycisków do okna
        add(newGameButton_Michala);
        add(newGameButton_Mateusza);
        add(newGameButton_Marcina);
        add(exitButton);
    }

    // Obsługa zdarzeń przycisków
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton_Michala) {
            // Tutaj kod rozpoczynający nową grę
            JOptionPane.showMessageDialog(this, "Nowa gra rozpoczęta!");
        } else if (e.getSource() == exitButton) {
            // Tutaj kod wychodzący z gry
            System.exit(0);
        }else if (e.getSource()==newGameButton_Mateusza)
        {
            JOptionPane.showMessageDialog(this, "Nowa gra rozpoczęta!");
        }else if (e.getSource()==newGameButton_Marcina)
        {
            JOptionPane.showMessageDialog(this, "Nowa gra rozpoczęta!");
        }
    }

}
