package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class GameMichalStart extends JFrame {

    public GameMichalStart() {
        // Podstawowa konfiguracja okna gry
        setTitle("Gra Michała");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Metoda startująca grę Michała.
     */
    public void startGame() {
        // Dodaj panel z logiką/rysowaniem
        add(new GameMichalPanel());

        // Na końcu ustawiamy widoczność okna na true
        setVisible(true);

        // Tu można uruchomić pętlę gry, wątek, timery itp.
        //new Thread(() -> gameLoop()).start();
    }
}
