package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Uruchomienie okna menu gry
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }
}