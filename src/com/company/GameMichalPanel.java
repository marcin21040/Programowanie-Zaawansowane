package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameMichalPanel extends JPanel {

    // --- Listy obiektów gry ---
    private List<MichalEnemy> enemies = new ArrayList<>();
    private List<MichalTower> towers = new ArrayList<>();
    private List<MichalProjectile> projectiles = new ArrayList<>();

    // Ścieżka (waypointy) dla wrogów
    private List<Point> enemyPath;

    // Główny Timer gry
    private Timer gameTimer;

    // --- Finanse ---
    private int money = 200;
    private int towerLimit = 5;
    private int defaultTowerCost = 50;
    private long lastPassiveIncomeTime = 0;
    private long passiveIncomeInterval = 3000;
    private int passiveIncomeAmount = 10;

    // --- Wrogowie (spawn) ---
    private int enemyStartX = 50;
    private int enemyStartY = 100;
    private int spawnInterval = 2000;
    private long lastSpawnTime = 0;

    // --- Warunki zwycięstwa/porażki ---
    private int baseHealth = 5;
    private int totalEnemiesToSpawn = 10;
    private int enemiesSpawned = 0;
    private int enemiesKilled = 0;
    private boolean gameOver = false;

    public GameMichalPanel() {
        setPreferredSize(new Dimension(800, 600));

        enemyPath = new ArrayList<>();
        enemyPath.add(new Point(50, 100));
        enemyPath.add(new Point(200, 100));
        enemyPath.add(new Point(200, 300));
        enemyPath.add(new Point(600, 300));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                placeTower(e.getX(), e.getY());
            }
        });

        gameTimer = new Timer(16, e -> {
            if (!gameOver) {
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

        // Wrogowie
        for (MichalEnemy enemy : enemies) {
            enemy.draw(g);
        }

        // Wieże
        for (MichalTower tower : towers) {
            tower.draw(g);
        }

        // Pociski
        for (MichalProjectile p : projectiles) {
            p.draw(g);
        }

        // Informacje tekstowe
        g.setColor(Color.BLACK);
        g.drawString("Pieniądze: " + money, 10, 20);
        g.drawString("Limit wież: " + towers.size() + "/" + towerLimit, 10, 40);
        g.drawString("Kliknij, aby postawić wieżę (koszt: " + defaultTowerCost + ")", 10, 60);
        g.drawString("Baza HP: " + baseHealth, 10, 80);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 36f));
            g.drawString("KONIEC GRY", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    private void updateGameLogic() {
        long currentTime = System.currentTimeMillis();

        // Pasywny dochód
        if (currentTime - lastPassiveIncomeTime >= passiveIncomeInterval) {
            money += passiveIncomeAmount;
            lastPassiveIncomeTime = currentTime;
        }

        // Spawn wrogów
        if (enemiesSpawned < totalEnemiesToSpawn) {
            if (currentTime - lastSpawnTime > spawnInterval) {
                spawnEnemy();
                lastSpawnTime = currentTime;
                enemiesSpawned++;
            }
        }

        // Ruch wrogów
        for (MichalEnemy enemy : enemies) {
            enemy.update();
        }

        // Strzały wież
        for (MichalTower tower : towers) {
            MichalEnemy target = findClosestEnemy(tower.getX(), tower.getY(), tower.getRange());
            if (target != null) {
                MichalProjectile shot = tower.shootAt(target);
                if (shot != null) {
                    projectiles.add(shot);
                }
            }
        }

        // Ruch i kolizje pocisków
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

        // Usuwanie pokonanych / dotarcie do końca
        Iterator<MichalEnemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            MichalEnemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                money += 20;
                enemiesKilled++;
                enemyIterator.remove();
            } else if (enemy.isPathFinished() || enemy.getX() > getWidth() + 50) {
                baseHealth--;
                enemyIterator.remove();
            }
        }

        // Usuwanie nieaktywnych pocisków
        projectiles.removeIf(p -> !p.isActive() || p.getX() > getWidth() + 50);

        // Sprawdzenie zwycięstwa / porażki
        checkVictoryOrDefeat();
    }

    private void checkVictoryOrDefeat() {
        if (baseHealth <= 0) {
            gameOver = true;
            gameTimer.stop();
            JOptionPane.showMessageDialog(this,
                    "Przegrałeś! Wrogowie zniszczyli Twoją bazę.",
                    "Koniec gry", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    private void placeTower(int x, int y) {
        if (gameOver) return;
        if (money >= defaultTowerCost && towers.size() < towerLimit) {
            towers.add(new MichalTower(x, y, defaultTowerCost));
            money -= defaultTowerCost;
        } else {
            System.out.println("Nie możesz postawić wieży! Brak środków lub osiągnięto limit wież.");
        }
    }

    private void spawnEnemy() {
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
