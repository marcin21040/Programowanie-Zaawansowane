package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MZ_GRA {

    public void rozpocznijGre() {
        SwingUtilities.invokeLater(RamkaGry::new);
    }
}

class RamkaGry extends JFrame {
    public RamkaGry() {
        setTitle("Strzelanka Kosmiczna");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new PanelGry());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class PanelGry extends JPanel implements ActionListener, KeyListener {
    private final int SZEROKOSC = 800;
    private final int WYSOKOSC = 600;
    private final int OPOZNIENIE = 15;

    private Timer zegar;
    private Statek statek;
    private ArrayList<Pocisk> pociski;

    public PanelGry() {
        setPreferredSize(new Dimension(SZEROKOSC, WYSOKOSC));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        statek = new Statek(SZEROKOSC / 2 - 25, WYSOKOSC - 80);
        pociski = new ArrayList<>();

        zegar = new Timer(OPOZNIENIE, this);
        zegar.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        statek.rysuj(g);
        for (Pocisk pocisk : pociski) {
            pocisk.rysuj(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        statek.rusz();
        for (int i = 0; i < pociski.size(); i++) {
            Pocisk pocisk = pociski.get(i);
            pocisk.rusz();
            if (pocisk.getY() < 0) {
                pociski.remove(i);
                i--;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int klawisz = e.getKeyCode();

        if (klawisz == KeyEvent.VK_LEFT) {
            statek.ustawDx(-5);
        } else if (klawisz == KeyEvent.VK_RIGHT) {
            statek.ustawDx(5);
        } else if (klawisz == KeyEvent.VK_SPACE) {
            pociski.add(new Pocisk(statek.getX() + statek.getSzerokosc() / 2 - 2, statek.getY()));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int klawisz = e.getKeyCode();

        if (klawisz == KeyEvent.VK_LEFT || klawisz == KeyEvent.VK_RIGHT) {
            statek.ustawDx(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

class Statek {
    private int x, y, dx;
    private final int SZEROKOSC = 50, WYSOKOSC = 50;
    private final Color KOLOR = Color.BLUE;

    public Statek(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rusz() {
        x += dx;
        if (x < 0) x = 0;
        if (x > 800 - SZEROKOSC) x = 800 - SZEROKOSC;
    }

    public void ustawDx(int dx) {
        this.dx = dx;
    }

    public void rysuj(Graphics g) {
        g.setColor(KOLOR);
        g.fillRect(x, y, SZEROKOSC, WYSOKOSC);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSzerokosc() {
        return SZEROKOSC;
    }
}

class Pocisk {
    private int x, y;
    private final int SZEROKOSC = 5, WYSOKOSC = 10;
    private final int PREDKOSC = 10;
    private final Color KOLOR = Color.RED;

    public Pocisk(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rusz() {
        y -= PREDKOSC;
    }

    public void rysuj(Graphics g) {
        g.setColor(KOLOR);
        g.fillRect(x, y, SZEROKOSC, WYSOKOSC);
    }

    public int getY() {
        return y;
    }
}

