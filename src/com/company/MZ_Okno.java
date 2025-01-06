package com.company;

import javax.swing.*;

public class MZ_Okno extends JFrame {

    public static int winWidth = 500;
    public static int winHeight = 600;

    public MZ_Okno(String nazwa){
        super(nazwa);

        setSize(winWidth, winHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }
}
