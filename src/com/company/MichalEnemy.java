package com.company;

import java.awt.*;

public class MichalEnemy {
    private int x;
    private int y;
    private int speed;
    private int health;
    private boolean alive = true;

    // Wielkość wroga (np. kwadrat 20x20)
    private int size = 20;

    public MichalEnemy(int x, int y, int speed, int health) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.health = health;
    }

    public void update() {
        // Prosty ruch w prawo
        x += speed;
        // Można rozbudować o ruch po ścieżce z zakrętami itp.
    }

    public void draw(Graphics g) {
        if (alive) {
            g.setColor(Color.RED);
            g.fillOval(x - size / 2, y - size / 2, size, size);
            // Ewentualnie pasek HP
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(health), x - 6, y - size / 2 - 2);
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) {
            alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    // gettery
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
