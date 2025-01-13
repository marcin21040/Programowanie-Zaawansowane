package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameMichalPanel extends JPanel {

    // ----- LISTY OBIEKTÓW GRY -----
    private List<MichalEnemy> enemies = new ArrayList<>();
    private List<MichalTower> towers = new ArrayList<>();
    private List<MichalProjectile> projectiles = new ArrayList<>();

    // Ścieżka, po której poruszają się wrogowie
    private List<Point> enemyPath;

    // Główny Timer gry (ok. 60 fps)
    private Timer gameTimer;

    // -------------- FINANSE --------------
    private int money = 200;
    private int towerLimit = 5;
    private int defaultTowerCost = 50;

    private long lastPassiveIncomeTime = 0;
    private long passiveIncomeInterval = 3000;
    private int passiveIncomeAmount = 10;

    // ----------- SPAWN WROGÓW ------------
    private int enemyStartX = 50;
    private int enemyStartY = 100;
    private int spawnInterval = 2000;
    private long lastSpawnTime = 0;

    // ------------- ZWYCIĘSTWO / PORAŻKA -------------
    /** Liczba punktów życia bazy gracza. */
    private int baseHealth = 5;

    /** Całkowita liczba wrogów, których chcemy wyspawnować. */
    private int totalEnemiesToSpawn = 10;

    /** Liczba już stworzonych wrogów. */
    private int enemiesSpawned = 0;

    /** Liczba zabitych wrogów (do sprawdzenia wygranej). */
    private int enemiesKilled = 0;

    /** Flaga oznaczająca koniec gry (wygrałeś/przegrałeś). */
    private boolean gameOver = false;

    public GameMichalPanel() {
        setPreferredSize(new Dimension(800, 600));

        // Przykładowa ścieżka (waypointy) dla wrogów
        enemyPath = new ArrayList<>();
        enemyPath.add(new Point(50, 100));
        enemyPath.add(new Point(200, 100));
        enemyPath.add(new Point(200, 300));
        enemyPath.add(new Point(600, 300));

        // Obsługa kliknięć myszą (stawianie wież)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTower(e.getX(), e.getY());
            }
        });

        // Inicjalizacja timera
        gameTimer = new Timer(16, e -> {
            if (!gameOver) {   // aktualizuj logikę tylko, jeśli gra nie jest zakończona
                updateGameLogic();
            }
            repaint();
        });
        gameTimer.start();
    }

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

        // Interfejs tekstowy
        g.setColor(Color.BLACK);
        g.drawString("Pieniądze: " + money, 10, 20);
        g.drawString("Limit wież: " + towers.size() + "/" + towerLimit, 10, 40);
        g.drawString("Kliknij, aby postawić wieżę (koszt: " + defaultTowerCost + ")", 10, 60);
        g.drawString("Baza HP: " + baseHealth, 10, 80);

        // Jeśli gra się skończyła, wyświetl komunikat
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 36f));
            g.drawString("KONIEC GRY", getWidth()/2 - 100, getHeight()/2);
        }
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

        // 2. Spawn nowych wrogów co 'spawnInterval', ale tylko do totalEnemiesToSpawn
        if (enemiesSpawned < totalEnemiesToSpawn) {
            if (currentTime - lastSpawnTime > spawnInterval) {
                spawnEnemy();
                lastSpawnTime = currentTime;
                enemiesSpawned++;
            }
        }

        // 3. Aktualizuj wrogów
        for (MichalEnemy enemy : enemies) {
            enemy.update();
        }

        // 4. Wieże strzelają
        for (MichalTower tower : towers) {
            MichalEnemy target = findClosestEnemy(tower.getX(), tower.getY(), tower.getRange());
            if (target != null) {
                MichalProjectile shot = tower.shootAt(target);
                if (shot != null) {
                    projectiles.add(shot);
                }
            }
        }

        // 5. Pociski: ruch i kolizje
        for (MichalProjectile p : projectiles) {
            p.update();
            for (MichalEnemy enemy : enemies) {
                if (p.collidesWith(enemy)) {
                    enemy.takeDamage(p.getDamage());
                    p.setActive(false);
                    break;
                }
            }
        }

        // 6. Usuwanie pokonanych wrogów i przyznawanie pieniędzy
        Iterator<MichalEnemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            MichalEnemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                money += 20;  // bonus za zabicie
                enemiesKilled++;
                enemyIterator.remove();
            }
            // Wróg przeszedł całą ścieżkę lub wyszedł poza mapę -> tracimy HP bazy
            else if (enemy.isPathFinished() || enemy.getX() > getWidth() + 50) {
                baseHealth--;
                enemyIterator.remove();
            }
        }

        // 7. Usuwanie nieaktywnych pocisków
        projectiles.removeIf(p -> !p.isActive() || p.getX() > getWidth() + 50);

        // 8. Sprawdź warunki zwycięstwa/porażki
        checkVictoryOrDefeat();
    }

    /**
     * Sprawdza, czy gra się już skończyła (zwycięstwo lub porażka).
     */
    private void checkVictoryOrDefeat() {
        // Porażka: baza HP <= 0
        if (baseHealth <= 0) {
            gameOver = true;
            gameTimer.stop();
            JOptionPane.showMessageDialog(this,
                    "Przegrałeś! Wrogowie zniszczyli Twoją bazę.",
                    "Koniec gry", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Zwycięstwo: wszyscy wrogowie już zostali wyspawnowani
        // i zabici (enemiesKilled == totalEnemiesToSpawn),
        // a na planszy nie ma żywych wrogów.
        if (enemiesSpawned >= totalEnemiesToSpawn
                && enemiesKilled >= totalEnemiesToSpawn
                && enemies.isEmpty()) {
            gameOver = true;
            gameTimer.stop();
            JOptionPane.showMessageDialog(this,
                    "Wygrałeś! Pokonałeś wszystkich wrogów!",
                    "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Metoda do stawiania wieży w miejscu kliknięcia.
     */
    private void placeTower(int x, int y) {
        if (gameOver) {
            return; // nie pozwalamy stawiać wież, jeśli gra się skończyła
        }
        if (money >= defaultTowerCost && towers.size() < towerLimit) {
            MichalTower newTower = new MichalTower(x, y, defaultTowerCost);
            towers.add(newTower);
            money -= defaultTowerCost;
        } else {
            System.out.println("Nie możesz postawić wieży! Brak środków lub osiągnięto limit wież.");
        }
    }

    /**
     * Tworzenie nowego wroga z listą waypointów.
     */
    private void spawnEnemy() {
        // Konstruktor wroga -> przekazujemy listę pointów (enemyPath)
        enemies.add(new MichalEnemy(enemyStartX, enemyStartY, enemyPath, 2, 100));
    }

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
