package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

public class MZ_GRA {

    public void rozpocznijGre() {
        SwingUtilities.invokeLater(RamkaGry::new);
    }
}


class HighScoreManager {
    private static final String HIGHSCORE_FILE = "highscore.txt";

    public static int odczytajHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    public static void zapiszHighScore(int highScore) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private JLabel predkoscLabel;
    private PanelGry panelGry;
    private JLabel highScoreLabel;

    public PanelPunktow(PanelGry panelGry) {
        this.panelGry = panelGry;
        setPreferredSize(new Dimension(150, 600));
        setBackground(Color.DARK_GRAY);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        punktyLabel = new JLabel("Punkty: 0");
        punktyLabel.setForeground(Color.WHITE);
        punktyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(punktyLabel, gbc);

        gbc.gridy = 1;
        predkoscLabel = new JLabel("Prędkość: 1.0");
        predkoscLabel.setForeground(Color.WHITE);
        predkoscLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(predkoscLabel, gbc);

        gbc.gridy = 2;

        // Wczytaj najwyższy wynik z pliku
        int zapisanyHighScore = wczytajHighScoreZPliku();
        highScoreLabel = new JLabel("High Score: " + zapisanyHighScore);
        highScoreLabel.setForeground(Color.WHITE);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(highScoreLabel, gbc);
    }

    private int wczytajHighScoreZPliku() {
        try {
            java.io.File file = new java.io.File("highscore.txt");
            if (file.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(file);
                if (scanner.hasNextInt()) {
                    return scanner.nextInt(); // Odczytaj najwyższy wynik z pliku
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // Jeśli plik nie istnieje lub jest pusty, zwróć 0
    }


    public void aktualizujHighScore(int highScore) {
        highScoreLabel.setText("High Score: " + highScore);
        repaint();
    }

    public void aktualizujPunkty(int punkty) {
        punktyLabel.setText("Punkty: " + punkty);
        repaint();
    }

    public void aktualizujPredkosc(double predkosc) {
        int predkoscInt = (int)(predkosc * 10) - 10 ;
        predkoscLabel.setText("Prędkość: " + predkoscInt);
        repaint();
    }
}

class PanelGry extends JPanel implements ActionListener, KeyListener {
    private final int SZEROKOSC = 800;
    private final int WYSOKOSC = 600;
    private final int OPOZNIENIE = 15;
    private final int CZESTOTLIWOSC_WROGOW = 100;
    private final int CZESTOTLIWOSC_KOMET = 200;
    private final int CZESTOTLIWOSC_GWIAZD = 20;
    private int highScore;
    private int punkty = 0;
    private Timer punktowyZegar;
    private PanelPunktow panelPunktow;

    private Timer zegar;
    private Statek statek;
    private ArrayList<Pocisk> pociski;
    private ArrayList<Kosmita> enemies;
    private ArrayList<Kometa> komety;
    private ArrayList<Gwiazda> gwiazdy;
    private int enemySpawnCounter = 0;
    private int kometaSpawnCounter = 0;
    private int gwiazdaSpawnCounter = 0;
    private boolean graTrwa = true;
    private double predkoscGry = 1.0;
    private ArrayList<Bonus> bonusy;
    private final int CZESTOTLIWOSC_BONUSOW = 270;
    private int bonusSpawnCounter = 0;

    public PanelGry() {
        setPreferredSize(new Dimension(SZEROKOSC, WYSOKOSC));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        bonusy = new ArrayList<>();

        statek = new Statek(SZEROKOSC / 2 - 25, WYSOKOSC - 80);
        pociski = new ArrayList<>();
        enemies = new ArrayList<>();
        komety = new ArrayList<>();
        gwiazdy = new ArrayList<>();
        highScore = HighScoreManager.odczytajHighScore();
        if (punkty > highScore) {
            highScore = punkty;
            if (panelPunktow != null) {
                panelPunktow.aktualizujHighScore(highScore);
            }
        }

        zegar = new Timer(OPOZNIENIE, this);
        zegar.start();


        punktowyZegar = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graTrwa) {
                    punkty += 1;
                    if (panelPunktow != null) {
                        panelPunktow.aktualizujPunkty(punkty);
                    }

                    if (punkty > highScore) {
                        highScore = punkty;
                        if (panelPunktow != null) {
                            panelPunktow.aktualizujHighScore(highScore);
                        }
                        HighScoreManager.zapiszHighScore(highScore);
                    }
                }
            }
        });
        punktowyZegar.start();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graTrwa) {

            for (Bonus bonus : bonusy) {
                bonus.rysuj(g);
            }

            for (Gwiazda gwiazda : gwiazdy) {
                gwiazda.rysuj(g);
            }

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
            g.drawString("Najlepszy wynik: " + highScore, SZEROKOSC / 2 - 150, WYSOKOSC / 2 + 50);
        }
    }

    public void setPanelPunktow(PanelPunktow panelPunktow) {
        this.panelPunktow = panelPunktow;
    }

    private void spawnBonus() {
        int x = (int) (Math.random() * (SZEROKOSC - Bonus.ROZMIAR));
        bonusy.add(new Bonus(x, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!graTrwa) return;

        bonusSpawnCounter++;
        if (bonusSpawnCounter >= CZESTOTLIWOSC_BONUSOW) {
            spawnBonus();
            bonusSpawnCounter = 0;
        }

        // Ruch i obsługa bonusów
        Iterator<Bonus> bonusIterator = bonusy.iterator();
        while (bonusIterator.hasNext()) {
            Bonus bonus = bonusIterator.next();
            bonus.rusz(predkoscGry);

            if (bonus.getBounds().intersects(statek.getBounds())) {
                // Gracz zebrał bonus
                punkty += 5;
                if (panelPunktow != null) {
                    panelPunktow.aktualizujPunkty(punkty);
                }
                bonusIterator.remove();
            } else if (bonus.getY() > WYSOKOSC) {
                // Bonus wyszedł poza ekran
                bonusIterator.remove();
            }
        }

        statek.rusz();

        predkoscGry += 0.001;
        if (panelPunktow != null) {
            panelPunktow.aktualizujPredkosc(predkoscGry);
        }

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

        gwiazdaSpawnCounter++;
        if (gwiazdaSpawnCounter >= CZESTOTLIWOSC_GWIAZD) {
            spawnGwiazda();
            gwiazdaSpawnCounter = 0;
        }

        // Ruch gwiazd
        Iterator<Gwiazda> gwiazdaIterator = gwiazdy.iterator();
        while (gwiazdaIterator.hasNext()) {
            Gwiazda gwiazda = gwiazdaIterator.next();
            gwiazda.rusz(predkoscGry);
            if (gwiazda.getY() > WYSOKOSC) {
                gwiazdaIterator.remove();
            }
        }


        Iterator<Kosmita> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Kosmita kosmita = enemyIterator.next();
            kosmita.rusz(predkoscGry);

            Iterator<Pocisk> pociskIterator = pociski.iterator();
            while (pociskIterator.hasNext()) {
                Pocisk pocisk = pociskIterator.next();
                if (kosmita.getBounds().intersects(pocisk.getBounds())) {
                    enemyIterator.remove();
                    pociskIterator.remove();

                    punkty += 10; // Dodanie punktów
                    if (panelPunktow != null) {
                        panelPunktow.aktualizujPunkty(punkty);
                        panelPunktow.repaint();
                    }

                    // Sprawdź i zaktualizuj najwyższy wynik
                    if (punkty > highScore) {
                        highScore = punkty;
                        if (panelPunktow != null) {
                            panelPunktow.aktualizujHighScore(highScore);
                        }
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


        Iterator<Kometa> kometaIterator = komety.iterator();
        while (kometaIterator.hasNext()) {
            Kometa kometa = kometaIterator.next();
            kometa.rusz(predkoscGry);

            if (kometa.getBounds().intersects(statek.getBounds())) {
                graTrwa = false;
                zegar.stop();
            }

            if (kometa.getY() > WYSOKOSC) {
                kometaIterator.remove();
            }
        }

        if (!graTrwa) {
            if (punkty > highScore) {
                highScore = punkty;
                HighScoreManager.zapiszHighScore(highScore);
            }
            repaint();
            return;
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

    private void spawnGwiazda() {
        int x = (int) (Math.random() * SZEROKOSC);
        gwiazdy.add(new Gwiazda(x, 0));
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

    public void rusz(double predkoscGry) {
        y += PREDKOSC * predkoscGry;
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

    public void rusz(double predkoscGry) {
        y += PREDKOSC * predkoscGry;
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

class Gwiazda {
    private int x, y;
    private final int ROZMIAR = 2;
    private final int PREDKOSC = 1;
    private final Color KOLOR = Color.WHITE;

    public Gwiazda(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rusz(double predkoscGry) {
        y += PREDKOSC * predkoscGry;
    }

    public void rysuj(Graphics g) {
        g.setColor(KOLOR);
        g.fillRect(x, y, ROZMIAR, ROZMIAR);
    }

    public int getY() {
        return y;
    }
}

class Bonus {
    public static final int ROZMIAR = 20;
    private int x, y;
    private final int PREDKOSC = 2;
    private final Color KOLOR = Color.YELLOW;

    public Bonus(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rusz(double predkoscGry) {
        y += PREDKOSC * predkoscGry;
    }

    public void rysuj(Graphics g) {
        g.setColor(KOLOR);
        g.fillRect(x, y, ROZMIAR, ROZMIAR);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ROZMIAR, ROZMIAR);
    }

    public int getY() {
        return y;
    }
}
