package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
    private final int CZESTOTLIWOSC_BONUSOW = 270;

    private int punkty = 0;
    private int highScore;
    private Timer zegar, punktowyZegar;
    private PanelPunktow panelPunktow;
    private boolean graTrwa = true;
    private double predkoscGry = 1.0;
    private Statek statek;
    private ArrayList<ObiektGry> obiektyGry = new ArrayList<>();
    private int enemySpawnCounter = 0, kometaSpawnCounter = 0, gwiazdaSpawnCounter = 0, bonusSpawnCounter = 0;

    public PanelGry() {
        setPreferredSize(new Dimension(SZEROKOSC, WYSOKOSC));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        statek = new Statek(SZEROKOSC / 2 - 25, WYSOKOSC - 80);
        highScore = HighScoreManager.odczytajHighScore();

        zegar = new Timer(OPOZNIENIE, this);
        zegar.start();

        punktowyZegar = new Timer(500, e -> {
            if (graTrwa) {
                punkty++;
                panelPunktow.aktualizujPunkty(punkty);
                if (punkty > highScore) {
                    highScore = punkty;
                    panelPunktow.aktualizujHighScore(highScore);
                    HighScoreManager.zapiszHighScore(highScore);
                }
            }
        });
        punktowyZegar.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graTrwa) {
            statek.rysuj(g);
            for (ObiektGry obiekt : obiektyGry) {
                obiekt.rysuj(g);
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

    private <T extends ObiektGry> void spawnObiekt(Class<T> typ, int x, int y) {
        try {
            obiektyGry.add(typ.getDeclaredConstructor(int.class, int.class).newInstance(x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!graTrwa) return;

        bonusSpawnCounter++;
        if (bonusSpawnCounter >= CZESTOTLIWOSC_BONUSOW) {
            spawnObiekt(Bonus.class, (int) (Math.random() * (SZEROKOSC - Bonus.ROZMIAR)), 0);
            bonusSpawnCounter = 0;
        }

        enemySpawnCounter++;
        if (enemySpawnCounter >= CZESTOTLIWOSC_WROGOW) {
            spawnObiekt(Kosmita.class, (int) (Math.random() * (SZEROKOSC - Kosmita.SZEROKOSC)), 0);
            enemySpawnCounter = 0;
        }

        kometaSpawnCounter++;
        if (kometaSpawnCounter >= CZESTOTLIWOSC_KOMET) {
            spawnObiekt(Kometa.class, (int) (Math.random() * (SZEROKOSC - Kometa.SZEROKOSC)), 0);
            kometaSpawnCounter = 0;
        }

        gwiazdaSpawnCounter++;
        if (gwiazdaSpawnCounter >= CZESTOTLIWOSC_GWIAZD) {
            spawnObiekt(Gwiazda.class, (int) (Math.random() * SZEROKOSC), 0);
            gwiazdaSpawnCounter = 0;
        }

        // Lista do usunięcia obiektów
        ArrayList<ObiektGry> doUsuniecia = new ArrayList<>();

        for (ObiektGry obiekt : obiektyGry) {
            obiekt.rusz(predkoscGry);

            if (obiekt instanceof Pocisk) {
                for (ObiektGry enemy : obiektyGry) {
                    if ((enemy instanceof Kosmita) && obiekt.getBounds().intersects(enemy.getBounds())) {
                        doUsuniecia.add(enemy);
                        doUsuniecia.add(obiekt);
                        punkty += 10;
                        panelPunktow.aktualizujPunkty(punkty);
                        break;
                    }
                }
            }

            if (obiekt instanceof Bonus && obiekt.getBounds().intersects(statek.getBounds())) {
                punkty += 5;
                panelPunktow.aktualizujPunkty(punkty);
                doUsuniecia.add(obiekt);
            }

            if (obiekt instanceof Kosmita || obiekt instanceof Kometa) {
                if (obiekt.getBounds().intersects(statek.getBounds())) {
                    graTrwa = false;
                    zegar.stop();
                }
            }

            if (obiekt.getY() > WYSOKOSC) {
                doUsuniecia.add(obiekt);
            }
        }

        // Usunięcie obiektów po iteracji
        obiektyGry.removeAll(doUsuniecia);

        statek.rusz();
        predkoscGry += 0.001;
        panelPunktow.aktualizujPredkosc(predkoscGry);

        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!graTrwa) return;
        int klawisz = e.getKeyCode();
        if (klawisz == KeyEvent.VK_LEFT) statek.ustawDx(-5);
        else if (klawisz == KeyEvent.VK_RIGHT) statek.ustawDx(5);
        else if (klawisz == KeyEvent.VK_SPACE)
            obiektyGry.add(new Pocisk(statek.getX() + statek.getSzerokosc() / 2 - 2, statek.getY() - 10));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            statek.ustawDx(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}




abstract class ObiektGry {
    protected int x, y;
    protected int szerokosc, wysokosc;
    protected int predkosc;
    protected Image obraz;

    public ObiektGry(int x, int y, int szerokosc, int wysokosc, int predkosc, String sciezkaObrazu) {
        this.x = x;
        this.y = y;
        this.szerokosc = szerokosc;
        this.wysokosc = wysokosc;
        this.predkosc = predkosc;

        if (sciezkaObrazu != null) {
            this.obraz = new ImageIcon(sciezkaObrazu).getImage();
        }
    }

    public void rusz(double predkoscGry) {
        y += predkosc * predkoscGry;
    }

    public void rysuj(Graphics g) {
        if (obraz != null) {
            g.drawImage(obraz, x, y, szerokosc, wysokosc, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, szerokosc, wysokosc);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, szerokosc, wysokosc);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getSzerokosc(){
        return szerokosc;
    }

}

class Kometa extends ObiektGry {
    public static final int SZEROKOSC = 50, WYSOKOSC = 50;
    private static final int PREDKOSC = 3;

    public Kometa(int x, int y) {
        super(x, y, SZEROKOSC, WYSOKOSC, PREDKOSC, "textures/kamien.png");
    }
}

class Kosmita extends ObiektGry {
    public static final int SZEROKOSC = 40, WYSOKOSC = 40;
    private static final int PREDKOSC = 2;

    public Kosmita(int x, int y) {
        super(x, y, SZEROKOSC, WYSOKOSC, PREDKOSC, "textures/kosmita.png");
    }
}

class Bonus extends ObiektGry {
    public static final int ROZMIAR = 20;
    private static final int PREDKOSC = 2;

    public Bonus(int x, int y) {
        super(x, y, ROZMIAR, ROZMIAR, PREDKOSC, "textures/moneta.png");
    }
}

class Gwiazda extends ObiektGry {
    private static final int ROZMIAR = 2;
    private static final int PREDKOSC = 1;

    public Gwiazda(int x, int y) {
        super(x, y, ROZMIAR, ROZMIAR, PREDKOSC, null);
    }

    @Override
    public void rysuj(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, szerokosc, wysokosc);
    }
}

class Statek extends ObiektGry {
    private int dx;

    public Statek(int x, int y) {
        super(x, y, 50, 50, 0, "textures/rakieta.png");
    }


    public void rusz() {
        x += dx;
        if (x < 0) x = 0;
        if (x > 800 - szerokosc) x = 800 - szerokosc;
    }

    public void ustawDx(int dx) {
        this.dx = dx;
    }


}

class Pocisk extends ObiektGry {

    private static final int PREDKOSC = -10;

    public Pocisk(int x, int y) {
        super(x, y, 5, 10, PREDKOSC, null);
    }

    @Override
    public void rusz(double predkoscGry) {
        y += predkosc;
    }

    @Override
    public void rysuj(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, szerokosc, wysokosc);
    }
}



