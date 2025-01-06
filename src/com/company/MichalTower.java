package com.company;

import java.awt.*;

public class MichalTower {
    private int x;
    private int y;
    private int range = 100;       // zasięg wieży
    private int damage = 25;       // obrażenia pocisku
    private int cooldown = 1000;   // czas w ms pomiędzy strzałami
    private long lastShotTime = 0;

    // Wielkość wieży
    private int size = 30;

    public MichalTower(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x - size / 2, y - size / 2, size, size);

        // Zasięg (opcjonalnie narysuj okrąg, żeby widzieć, gdzie sięga wieża)
        g.setColor(new Color(0, 0, 255, 50));
        g.drawOval(x - range, y - range, range * 2, range * 2);
    }

    /**
     * Próba strzału w wybranego wroga. Zwraca obiekt MichalProjectile,
     * jeśli udało się strzelić (cooldown minął), lub null w przeciwnym razie.
     */
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
}
