package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameMichalPanel extends JPanel {

    // Listy przechowujące obiekty w grze
    private java.util.List<MichalEnemy> enemies = new ArrayList<>();
    private java.util.List<MichalTower> towers = new ArrayList<>();
    private java.util.List<MichalProjectile> projectiles = new ArrayList<>();
    private java.util.List<Point> path = new ArrayList<>();



    private Timer gameTimer;

    // Przykładowe współrzędne, gdzie wrogowie startują (po lewej)
    private int enemyStartX = 50;
    private int enemyStartY = 100;

    // Czas (w milisekundach) co jaki pojawia się nowy wróg
    private int spawnInterval = 2000;
    private long lastSpawnTime = 0;

    public GameMichalPanel() {
        setPreferredSize(new Dimension(800, 600));

        // Initialize the path
        path.add(new Point(50, 100));
        path.add(new Point(200, 100));
        path.add(new Point(200, 300));
        path.add(new Point(600, 300));

        // Dodajemy MouseListener, żeby np. stawiać wieże klikając w panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTower(e.getX(), e.getY());
            }
        });

        // Timer (ok. 60 FPS = 16 ms)
        gameTimer = new Timer(16, e -> {
            updateGameLogic();
            repaint();
        });
        gameTimer.start();
    }

    /**
     * Metoda rysująca komponent (planszę, wrogów, wieże, pociski).
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Rysowanie "tła"
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Rysuj wrogów
        for (MichalEnemy enemy : enemies) {
            enemy.draw(g);
        }

        // Rysuj wieże
        for (MichalTower tower : towers) {
            tower.draw(g);
        }

        // Rysuj pociski
        for (MichalProjectile p : projectiles) {
            p.draw(g);
        }

        // Przykładowy napis:
        g.setColor(Color.BLACK);
        g.drawString("Kliknij na planszy, aby postawić wieżę.", 10, 20);
    }

    /**
     * Główna metoda aktualizacji logiki gry.
     */
    private void updateGameLogic() {
        // 1. Spawn nowych wrogów co określony czas
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > spawnInterval) {
            spawnEnemy();
            lastSpawnTime = currentTime;
        }

        // 2. Aktualizuj pozycje wrogów
        for (MichalEnemy enemy : enemies) {
            enemy.update();
        }

        // 3. Aktualizuj wieże (np. czy strzelają do wroga, generują pociski)
        for (MichalTower tower : towers) {
            // Znajdź najbliższego wroga w zasięgu
            MichalEnemy target = findClosestEnemy(tower.getX(), tower.getY(), tower.getRange());
            if (target != null) {
                // Wieża może strzelić (jeśli cooldown minął)
                MichalProjectile shot = tower.shootAt(target);
                if (shot != null) {
                    projectiles.add(shot);
                }
            }
        }

        // 4. Aktualizuj pociski (ruch i kolizje)
        for (MichalProjectile p : projectiles) {
            p.update();
            // Sprawdź kolizję z wrogami
            for (MichalEnemy enemy : enemies) {
                if (p.collidesWith(enemy)) {
                    enemy.takeDamage(p.getDamage());
                    // Oznacz pocisk do usunięcia (bo np. trafił)
                    p.setActive(false);
                    break;
                }
            }
        }

        // 5. Usuń nieaktywnych wrogów (pokonani lub wyszli poza planszę)
        enemies.removeIf(enemy -> !enemy.isAlive() || enemy.getX() > getWidth() + 50);

        // 6. Usuń nieaktywne pociski
        projectiles.removeIf(p -> !p.isActive() || p.getX() > getWidth() + 50);

        // 7. Ewentualnie: warunki zwycięstwa / porażki, itp.
    }

    /**
     * Metoda do stawiania wieży w miejscu kliknięcia.
     */
    private void placeTower(int x, int y) {
        towers.add(new MichalTower(x, y));
    }

    /**
     * Spawnowanie nowego wroga na początkowych współrzędnych.
     */
    private void spawnEnemy() {
        enemies.add(new MichalEnemy(
                50,                    // start X
                100,                   // start Y
                path,                  // cała lista punktów
                2.0,                   // speed
                100                    // maxHealth
        ));
    }

    /**
     * Metoda szukająca najbliższego wroga w zasięgu (distance <= range).
     */
    private MichalEnemy findClosestEnemy(int towerX, int towerY, int range) {
        MichalEnemy closest = null;
        double closestDist = Double.MAX_VALUE;
        for (MichalEnemy enemy : enemies) {
            double dist = distance(towerX, towerY, enemy.getX(), enemy.getY());
            if (dist < range && dist < closestDist) {
                closestDist = dist;
                closest = enemy;
            }
        }
        return closest;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
