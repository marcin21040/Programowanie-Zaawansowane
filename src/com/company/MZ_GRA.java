package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

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

        PanelGry panelGry = new PanelGry();
        PanelPunktow panelPunktow = new PanelPunktow(panelGry);
        panelGry.setPanelPunktow(panelPunktow);

        setLayout(new BorderLayout());
        add(panelGry, BorderLayout.CENTER);
        add(panelPunktow, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}


class PanelPunktow extends JPanel {
    private JLabel punktyLabel;
    private PanelGry panelGry;

    public PanelPunktow(PanelGry panelGry) {
        this.panelGry = panelGry;
        setPreferredSize(new Dimension(150, 600));
        setBackground(Color.DARK_GRAY);
        setLayout(new GridBagLayout());

        punktyLabel = new JLabel("Punkty: 0");
        punktyLabel.setForeground(Color.WHITE);
        punktyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(punktyLabel);
    }

    public void aktualizujPunkty(int punkty) {
        punktyLabel.setText("Punkty: " + punkty);
        repaint();
    }
}

class PanelGry extends JPanel implements ActionListener, KeyListener {
    private final int SZEROKOSC = 800;
    private final int WYSOKOSC = 600;
    private final int OPOZNIENIE = 15;
    private final int CZESTOTLIWOSC_WROGOW = 100;
    private final int CZESTOTLIWOSC_KOMET = 200;
    private int punkty = 0;
    private PanelPunktow panelPunktow;

    private Timer zegar;
    private Statek statek;
    private ArrayList<Pocisk> pociski;
    private ArrayList<Kosmita> enemies;
    private ArrayList<Kometa> komety;
    private int enemySpawnCounter = 0;
    private int kometaSpawnCounter = 0;
    private boolean graTrwa = true;

    public PanelGry() {
        setPreferredSize(new Dimension(SZEROKOSC, WYSOKOSC));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        statek = new Statek(SZEROKOSC / 2 - 25, WYSOKOSC - 80);
        pociski = new ArrayList<>();
        enemies = new ArrayList<>();
        komety = new ArrayList<>();

        zegar = new Timer(OPOZNIENIE, this);
        zegar.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graTrwa) {
            statek.rysuj(g);
            for (Pocisk pocisk : pociski) {
                pocisk.rysuj(g);
            }
            for (Kosmita kosmita : enemies) {
                kosmita.rysuj(g);
            }
            for (Kometa kometa : komety) {
                kometa.rysuj(g);
            }
        } else {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("KONIEC GRY!", SZEROKOSC / 2 - 100, WYSOKOSC / 2);
        }
    }

    public void setPanelPunktow(PanelPunktow panelPunktow) {
        this.panelPunktow = panelPunktow;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!graTrwa) return;

        statek.rusz();

        // Ruch pocisków
        for (int i = 0; i < pociski.size(); i++) {
            Pocisk pocisk = pociski.get(i);
            pocisk.rusz();
            if (pocisk.getY() < 0) {
                pociski.remove(i);
                i--;
            }
        }

        enemySpawnCounter++;
        if (enemySpawnCounter >= CZESTOTLIWOSC_WROGOW) {
            spawnEnemy();
            enemySpawnCounter = 0;
        }

        kometaSpawnCounter++;
        if (kometaSpawnCounter >= CZESTOTLIWOSC_KOMET) {
            spawnKometa();
            kometaSpawnCounter = 0;
        }

        // Obsługa kolizji kosmitów
        Iterator<Kosmita> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Kosmita kosmita = enemyIterator.next();
            kosmita.rusz();

            Iterator<Pocisk> pociskIterator = pociski.iterator();
            while (pociskIterator.hasNext()) {
                Pocisk pocisk = pociskIterator.next();
                if (kosmita.getBounds().intersects(pocisk.getBounds())) {
                    enemyIterator.remove();
                    pociskIterator.remove();

                    punkty += 10;
                    if (panelPunktow != null) {
                        panelPunktow.aktualizujPunkty(punkty);
                        panelPunktow.repaint();
                    }
                    break;
                }
            }

            if (kosmita.getBounds().intersects(statek.getBounds())) {
                graTrwa = false;
                zegar.stop();
            }

            if (kosmita.getY() > WYSOKOSC) {
                enemyIterator.remove();
            }
        }

        // Obsługa ruchu komet
        Iterator<Kometa> kometaIterator = komety.iterator();
        while (kometaIterator.hasNext()) {
            Kometa kometa = kometaIterator.next();
            kometa.rusz();

            if (kometa.getBounds().intersects(statek.getBounds())) {
                graTrwa = false;
                zegar.stop();
            }

            if (kometa.getY() > WYSOKOSC) {
                kometaIterator.remove();
            }
        }

        repaint();
    }



    private void spawnEnemy() {
        int x = (int) (Math.random() * (SZEROKOSC - Kosmita.SZEROKOSC));
        enemies.add(new Kosmita(x, 0));
    }

    private void spawnKometa() {
        int x = (int) (Math.random() * (SZEROKOSC - Kometa.SZEROKOSC));
        komety.add(new Kometa(x, 0));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!graTrwa) return;

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

class Kometa {
    public static final int SZEROKOSC = 50, WYSOKOSC = 50;
    private int x, y;
    private final int PREDKOSC = 3;
    private final Color KOLOR = Color.GRAY;

    public Kometa(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rusz() {
        y += PREDKOSC;
    }

    public void rysuj(Graphics g) {
        g.setColor(KOLOR);
        g.fillRect(x, y, SZEROKOSC, WYSOKOSC);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SZEROKOSC, WYSOKOSC);
    }

    public int getY() {
        return y;
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

    public Rectangle getBounds() {
        return new Rectangle(x, y, SZEROKOSC, WYSOKOSC);
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

    public Rectangle getBounds() {
        return new Rectangle(x, y, SZEROKOSC, WYSOKOSC);
    }

    public int getY() {
        return y;
    }
}

class Kosmita {
    public static final int SZEROKOSC = 40, WYSOKOSC = 40;
    private int x, y;
    private final int PREDKOSC = 2;
    private final Color KOLOR = Color.GREEN;

    public Kosmita(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rusz() {
        y += PREDKOSC;
    }

    public void rysuj(Graphics g) {
        g.setColor(KOLOR);
        g.fillRect(x, y, SZEROKOSC, WYSOKOSC);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SZEROKOSC, WYSOKOSC);
    }

    public int getY() {
        return y;
    }
}
