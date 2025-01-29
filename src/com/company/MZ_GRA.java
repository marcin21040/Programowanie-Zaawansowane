package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface BonusItem {
    String value();
}

public class MZ_GRA {

    public void rozpocznijGre() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RamkaGry();
            }
        });
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
                    return scanner.nextInt();
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
    private static final int SZEROKOSC = 800, WYSOKOSC = 600, OPOZNIENIE = 15;
    private static final int[] CZESTOTLIWOSCI = {100, 200, 20, 270, 515}; // Kosmita, Kometa, Gwiazda, Bonus, Diament

    private int punkty = 0, highScore;
    private boolean graTrwa = true;
    private double predkoscGry = 1.0;
    private Statek statek;
    private ArrayList<ObiektGry> obiektyGry = new ArrayList<>();
    private int[] spawnCounters = new int[CZESTOTLIWOSCI.length];

    private String wyswietlanyKomunikat = "";
    private int czasWyswietlaniaKomunikatu = 0;

    private JButton przyciskWyjdz, przyciskRestart;
    private Timer zegar, punktowyZegar;
    private PanelPunktow panelPunktow;

    public PanelGry() {
        setPreferredSize(new Dimension(SZEROKOSC, WYSOKOSC));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        statek = new Statek(SZEROKOSC / 2 - 25, WYSOKOSC - 80);
        highScore = HighScoreManager.odczytajHighScore();

        zegar = new Timer(OPOZNIENIE, this);
        zegar.start();

        punktowyZegar = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graTrwa) {
                    aktualizujPunkty();
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

            if (czasWyswietlaniaKomunikatu > 0) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString(wyswietlanyKomunikat, SZEROKOSC / 2 - 100, 100);
                czasWyswietlaniaKomunikatu--;
            }
        } else {
            wyswietlEkranKoncowy(g);
        }
    }

    private void wyswietlEkranKoncowy(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("KONIEC GRY!", SZEROKOSC / 2 - 100, WYSOKOSC / 2);
        g.drawString("Najlepszy wynik: " + highScore, SZEROKOSC / 2 - 150, WYSOKOSC / 2 + 50);
        dodajPrzyciski();
    }

    private void dodajPrzyciski() {
        if (przyciskWyjdz != null) return;

        przyciskWyjdz = new JButton("Wyjdź");
        przyciskRestart = new JButton("Spróbuj ponownie");

        przyciskWyjdz.setBounds(SZEROKOSC / 2 - 75, WYSOKOSC / 2 + 100, 200, 40);
        przyciskRestart.setBounds(SZEROKOSC / 2 - 75, WYSOKOSC / 2 + 150, 200, 40);

        przyciskWyjdz.addActionListener(e -> {
            JFrame ramka = (JFrame) SwingUtilities.getWindowAncestor(PanelGry.this);
            if (ramka != null) {
                ramka.dispose();
            }
        });
        przyciskRestart.addActionListener(e -> restartGry());

        setLayout(null);
        add(przyciskWyjdz);
        add(przyciskRestart);
        repaint();
    }

    private void restartGry() {
        JFrame ramka = (JFrame) SwingUtilities.getWindowAncestor(this);
        ramka.dispose();
        SwingUtilities.invokeLater(RamkaGry::new);
    }

    public void setPanelPunktow(PanelPunktow panelPunktow) {
        this.panelPunktow = panelPunktow;
    }

    private void aktualizujPunkty() {
        punkty++;
        panelPunktow.aktualizujPunkty(punkty);
        if (punkty > highScore) {
            highScore = punkty;
            panelPunktow.aktualizujHighScore(highScore);
            HighScoreManager.zapiszHighScore(highScore);
        }
    }

    private void spawnObiekt(Class<? extends ObiektGry> typ, int x, int y) {
        try {
            obiektyGry.add(typ.getDeclaredConstructor(int.class, int.class).newInstance(x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!graTrwa) return;

        Class<?>[] klasyObiektow = {Kosmita.class, Kometa.class, Gwiazda.class, Bonus.class, Diament.class};
        for (int i = 0; i < spawnCounters.length; i++) {
            if (++spawnCounters[i] >= CZESTOTLIWOSCI[i]) {
                spawnObiekt((Class<? extends ObiektGry>) klasyObiektow[i], (int) (Math.random() * (SZEROKOSC - 50)), 0);
                spawnCounters[i] = 0;
            }
        }

        ArrayList<ObiektGry> doUsuniecia = new ArrayList<>();

        for (int i = 0; i < obiektyGry.size(); i++) {
            ObiektGry obiekt = obiektyGry.get(i);
            obiekt.rusz(predkoscGry);

            if (sprawdzKolizje(obiekt)) {
                doUsuniecia.add(obiekt);
            }
        }

        obiektyGry.removeAll(doUsuniecia);

        statek.rusz();
        predkoscGry += 0.001;
        panelPunktow.aktualizujPredkosc(predkoscGry);
        repaint();
    }


    private boolean sprawdzKolizje(ObiektGry obiekt) {
        if (obiekt instanceof Pocisk) {
            ArrayList<ObiektGry> doUsuniecia = new ArrayList<>();
            for (ObiektGry enemy : obiektyGry) {
                if (enemy instanceof Kosmita && obiekt.getBounds().intersects(enemy.getBounds())) {
                    punkty += 10;
                    panelPunktow.aktualizujPunkty(punkty);
                    doUsuniecia.add(enemy);
                }
            }
            obiektyGry.removeAll(doUsuniecia);
            return !doUsuniecia.isEmpty();
        }

        if (obiekt instanceof Bonus || obiekt instanceof Diament) {
            if (obiekt.getBounds().intersects(statek.getBounds())) {
                if (obiekt instanceof Bonus) {
                    punkty += 5;
                } else {
                    punkty += 100;
                }
                panelPunktow.aktualizujPunkty(punkty);
                BonusItem bonusItem = obiekt.getClass().getAnnotation(BonusItem.class);
                if (bonusItem != null) {
                    wyswietlanyKomunikat = bonusItem.value();
                }
                czasWyswietlaniaKomunikatu = 100;
                return true;
            }
        }

        if ((obiekt instanceof Kosmita || obiekt instanceof Kometa) && obiekt.getBounds().intersects(statek.getBounds())) {
            graTrwa = false;
            zegar.stop();
            return false;
        }

        return obiekt.getY() > WYSOKOSC;
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
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) statek.ustawDx(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}


class ObiektGry {
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

@BonusItem("Dodatkowe punkty!")
class Bonus extends ObiektGry {
    public static final int ROZMIAR = 20;
    private static final int PREDKOSC = 4;

    public Bonus(int x, int y) {
        super(x, y, ROZMIAR, ROZMIAR, PREDKOSC, "textures/moneta.png");
    }
}

@BonusItem("Mega Bonus! +100 punktów!")
class Diament extends ObiektGry {
    public static final int ROZMIAR = 30;
    private static final int PREDKOSC = 5;

    public Diament(int x, int y) {
        super(x, y, ROZMIAR, ROZMIAR, PREDKOSC, "textures/diament.png");
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
        super(x, y, 50, 50, 20, "textures/rakieta.png");
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



