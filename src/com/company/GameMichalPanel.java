package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameMichalPanel extends JPanel {

    // Listy przechowujące obiekty w grze
    private java.util.List<MichalEnemy> enemies = new ArrayList<>();
    private java.util.List<MichalTower> towers = new ArrayList<>();
    private java.util.List<MichalProjectile> projectiles = new ArrayList<>();
    private List<Point> enemyPath;
    private Timer gameTimer;

    // ------------------ FINANSE ------------------
    // Ilość pieniędzy gracza
    private int money = 200;
    // Maksymalna liczba wież (limit)
    private int towerLimit = 5;
    // Koszt jednej wieży (w tym przykładzie stały, ale można rozszerzyć)
    private int defaultTowerCost = 50;

    // Pasywne zarabianie:
    // co ile milisekund dostajemy "passiveIncomeAmount" pieniędzy
    private long lastPassiveIncomeTime = 0;
    private long passiveIncomeInterval = 3000; // co 3 sekundy
    private int passiveIncomeAmount = 10;      // dostaniemy 10 pieniędzy co 3 sekundy

    // ------------ KONIEC FINANSÓW ------------

    // Pozycja startowa wrogów (dla spawnowania)
    private int enemyStartX = 50;
    private int enemyStartY = 100;

    // Czas co jaki pojawia się nowy wróg
    private int spawnInterval = 2000;
    private long lastSpawnTime = 0;



    public GameMichalPanel() {
        setPreferredSize(new Dimension(800, 600));
        enemyPath = new ArrayList<>();
        enemyPath.add(new Point(50, 100));
        enemyPath.add(new Point(200, 100));
        enemyPath.add(new Point(200, 300));
        enemyPath.add(new Point(600, 300));
        // MouseListener do stawiania wież
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTower(e.getX(), e.getY());
            }
        });

        // Timer ~60 FPS
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

        // Tło
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

        // Interfejs – np. informacja o aktualnych pieniądzach i limicie wież
        g.setColor(Color.BLACK);
        g.drawString("Pieniądze: " + money, 10, 20);
        g.drawString("Limit wież: " + towers.size() + "/" + towerLimit, 10, 40);
        g.drawString("Kliknij na planszy, aby postawić wieżę (koszt " + defaultTowerCost + ")", 10, 60);
    }

    /**
     * Główna metoda aktualizacji logiki gry.
     */
    private void updateGameLogic() {
        long currentTime = System.currentTimeMillis();

        // 1. Pasywne dochody
        if (currentTime - lastPassiveIncomeTime >= passiveIncomeInterval) {
            money += passiveIncomeAmount;
            lastPassiveIncomeTime = currentTime;
        }

        // 2. Spawn nowych wrogów co 'spawnInterval'
        if (currentTime - lastSpawnTime > spawnInterval) {
            spawnEnemy();
            lastSpawnTime = currentTime;
        }

        // 3. Aktualizuj pozycje wrogów
        for (MichalEnemy enemy : enemies) {
            enemy.update();
        }

        // 4. Wieże strzelają
        for (MichalTower tower : towers) {
            // Znajdź najbliższego wroga w zasięgu
            MichalEnemy target = findClosestEnemy(tower.getX(), tower.getY(), tower.getRange());
            if (target != null) {
                // Wieża strzela (gdy cooldown minął)
                MichalProjectile shot = tower.shootAt(target);
                if (shot != null) {
                    projectiles.add(shot);
                }
            }
        }

        // 5. Aktualizuj pociski (ruch i kolizje)
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

        // 6. Usuwanie pokonanych wrogów i przyznawanie pieniędzy
        //    (lepiej robić to w pętli z iteratorem)
        Iterator<MichalEnemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            MichalEnemy enemy = enemyIterator.next();
            // Jeśli wróg jest martwy...
            if (!enemy.isAlive()) {
                // Zarabiamy dodatkowe pieniądze za zabicie
                money += 20; // np. 20 za zniszczenie przeciwnika
                enemyIterator.remove();
            }
            // Możesz też dodać warunek, jeśli wróg wyszedł poza mapę
            // i np. odejmuje HP bazie gracza.
            else if (enemy.getX() > getWidth() + 50) {
                enemyIterator.remove();
            }
        }

        // 7. Usuń nieaktywne pociski
        projectiles.removeIf(p -> !p.isActive() || p.getX() > getWidth() + 50);

        // 8. (opcjonalnie) sprawdź warunki zwycięstwa / porażki itp.
    }

    /**
     * Metoda do stawiania wieży w miejscu kliknięcia.
     */
    private void placeTower(int x, int y) {
        // Sprawdź, czy możemy postawić wieżę:
        // 1) czy mamy kasę
        // 2) czy nie przekraczamy limitu
        if (money >= defaultTowerCost && towers.size() < towerLimit) {
            // Tworzymy nową wieżę o domyślnym koszcie (np. 50)
            MichalTower newTower = new MichalTower(x, y, defaultTowerCost);
            towers.add(newTower);
            // Odejmij koszt
            money -= defaultTowerCost;
        } else {
            // Możesz wyświetlić komunikat o braku pieniędzy albo przekroczonym limicie
            System.out.println("Nie możesz postawić wieży! Brak środków lub osiągnięto limit wież.");
        }
    }

    /**
     * Spawnowanie nowego wroga w prostej wersji (bez ścieżki).
     * Jeśli chcesz dodać ścieżkę, przekazuj np. listę punktów do konstruktora wroga.
     */
    private void spawnEnemy() {
        enemies.add(new MichalEnemy(enemyStartX, enemyStartY, enemyPath, 2, 100));
        // Taki konstruktor: MichalEnemy(int x, int y, int speed, int health)
        // lub zmodyfikuj, jeśli masz u siebie wersję z listą punktów.
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
