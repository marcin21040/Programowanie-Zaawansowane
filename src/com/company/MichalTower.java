package com.company;

import java.awt.*;

public class MichalTower {
    private int x;
    private int y;
    private int range;       // zasięg wieży
    private int damage;       // obrażenia pocisku
    private int cooldown;   // czas w ms pomiędzy strzałami
    private long lastShotTime = 0;

    // Wielkość wieży
    private int size = 30;

    // Koszt wieży (jeśli chcemy różne wieże, można wprowadzić różne koszty w konstruktorze)
    private int cost;

    private TowerType towerType;

    public MichalTower(int x, int y, TowerType towerType) {
        this.x = x;
        this.y = y;
        this.towerType = towerType;

        // Ustawiamy konkretne parametry z enum:
        this.range = towerType.getRange();
        this.damage = towerType.getDamage();
        this.cooldown = towerType.getCooldown();
        this.cost = towerType.getCost();
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;  // Z rzutowaniem, by móc ewentualnie używać funkcji z Graphics2D

        int halfSize = size / 2;
        int leftX = x - halfSize;
        int topY  = y - halfSize;

        // --- Podstawa wieży (ciemnoszary prostokąt) ---
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(leftX, topY, size, size);

        // --- Górna część (np. lufa lub głowica) ---
        // Rysujemy mały „komin” na środku górnej krawędzi bazy.
        int turretWidth = size / 4;   // szerokość wieżyczki
        int turretHeight = size / 2;  // wysokość wieżyczki
        int turretX = x - (turretWidth / 2);
        int turretY = topY - (turretHeight / 2); // wystaje trochę ponad bazę
        g2d.setColor(new Color(70, 70, 70));     // jaśniejszy szary
        g2d.fillRect(turretX, turretY, turretWidth, turretHeight);

        // --- Ozdobny obrys wieży (opcjonalnie) ---
        g2d.setColor(Color.BLACK);
        g2d.drawRect(leftX, topY, size, size);
        g2d.drawRect(turretX, turretY, turretWidth, turretHeight);

        // --- "Działko" – niewielki prostokąt wychodzący z wieżyczki ---
        // Przykładowo pozioma lufa wychodząca z prawej strony górnej części wieżyczki
        int barrelWidth = size / 2;
        int barrelHeight = 6;
        int barrelX = turretX + turretWidth;       // wychodzi z prawej krawędzi wieżyczki
        int barrelY = turretY + (turretHeight / 4); // mniej więcej w 1/4 wysokości
        g2d.setColor(Color.GRAY);
        g2d.fillRect(barrelX, barrelY, barrelWidth, barrelHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(barrelX, barrelY, barrelWidth, barrelHeight);

        // --- "Kopuła" wieży (np. okrąg na środku) ---
        // Rysujemy okrąg na środku bazy, żeby wyglądała na obudowę wieżyczki
        int domeDiameter = size / 2;
        int domeX = x - (domeDiameter / 2);
        int domeY = y - (domeDiameter / 2);
        g2d.setColor(new Color(100, 100, 100, 150)); // trochę przezroczysty
        g2d.fillOval(domeX, domeY, domeDiameter, domeDiameter);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(domeX, domeY, domeDiameter, domeDiameter);

        // Zasięg (opcjonalnie narysuj okrąg, żeby widzieć, gdzie sięga wieża)
        g2d.setColor(new Color(0, 0, 255, 50));
        g2d.drawOval(x - range, y - range, range * 2, range * 2);
    }

    // Strzelanie do wroga
    public MichalProjectile shootAt(MichalEnemy enemy) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > cooldown) {
            lastShotTime = currentTime;
            // Tworzymy pocisk lecący z wieży w kierunku wroga
            return new MichalProjectile(x, y, (int) enemy.getX(), (int) enemy.getY(), damage);
        }
        return null;
    }

    // gettery
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRange() {
        return range;
    }

    public int getDamage() {
        return damage;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCost() {
        return cost;
    }

    public TowerType getTowerType() {
        return towerType;
    }
}
