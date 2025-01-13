package com.company;

import java.awt.*;

public class MichalProjectile {
    private double x;
    private double y;
    private double speed = 7.0;
    private int damage;
    private boolean active = true;

    // Cel - współrzędne wroga w momencie wystrzału
    private double targetX;
    private double targetY;

    // Do obliczeń kierunku lotu
    private double dx;
    private double dy;
    private double length;

    public MichalProjectile(int startX, int startY, int targetX, int targetY, int damage) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        this.targetX = targetX;
        this.targetY = targetY;

        // Obliczamy kierunek lotu (wektor dx/dy)
        dx = targetX - x;
        dy = targetY - y;
        length = Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            dx /= length;  // normalizacja wektora
            dy /= length;
        }
    }

    public void update() {
        // Poruszaj pocisk w kierunku celu
        x += dx * speed;
        y += dy * speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval((int)x - 5, (int)y - 5, 10, 10);
    }

    /**
     * Sprawdza prostym sposobem (odległość) czy pocisk trafił w wroga.
     */
    public boolean collidesWith(MichalEnemy enemy) {
        double dist = Math.sqrt(Math.pow(enemy.getX() - x, 2) + Math.pow(enemy.getY() - y, 2));
        return dist < 15; // próg zależny od rozmiaru wroga/pocisku
    }

    public int getDamage() {
        return damage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public double getX() {
        return x;
    }
}
