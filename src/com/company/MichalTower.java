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
        g.setColor(Color.BLUE);
        g.fillRect(x - size / 2, y - size / 2, size, size);

        // Zasięg (opcjonalnie narysuj okrąg, żeby widzieć, gdzie sięga wieża)
        g.setColor(new Color(0, 0, 255, 50));
        g.drawOval(x - range, y - range, range * 2, range * 2);
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
