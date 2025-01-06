package com.company;

import java.awt.*;
import java.util.List;

/**
 * Rozbudowany wróg typu "MichalEnemy".
 * - Porusza się po ustalonej ścieżce (lista punktów).
 * - Może być spowolniony na pewien czas (status effect).
 * - Ma pasek zdrowia i status alive/dead.
 */
public class MichalEnemy {
    private double x;
    private double y;

    // Aktualny punkt docelowy na ścieżce (indeks w liście points).
    private int currentCheckpointIndex = 0;

    // Ścieżka składająca się z kolejnych punktów (x, y),
    // przez które wróg przechodzi.
    private List<Point> path;

    // Podstawowe statystyki wroga
    private double baseSpeed;     // bazowa prędkość (np. 2.0)
    private double currentSpeed;  // aktualna prędkość (zmniejszana np. przez spowolnienia)
    private int maxHealth;        // maksymalne HP
    private int health;           // aktualne HP
    private boolean alive = true;

    // Wielkość wroga do rysowania
    private int size = 20;

    // Efekt spowolnienia – ile jeszcze ms będzie trwał
    private long slowEffectEndTime = 0;
    // Jak silny jest efekt spowolnienia (np. 0.5 oznacza 50% prędkości)
    private double slowMultiplier = 1.0;

    /**
     * Konstruktor przyjmujący:
     *  - punkt startowy (x, y)
     *  - listę punktów (ścieżka), przez które wróg przejdzie
     *  - prędkość, zdrowie
     */
    public MichalEnemy(double x, double y, List<Point> path, double speed, int health) {
        this.x = x;
        this.y = y;
        this.path = path;
        this.baseSpeed = speed;
        this.currentSpeed = speed; // na początku równy bazowej prędkości
        this.maxHealth = health;
        this.health = health;
    }

    /**
     * Metoda update:
     *  1. Sprawdza, czy wróg jest wciąż żywy.
     *  2. Aktualizuje ewentualne efekty spowolnienia.
     *  3. Porusza się w kierunku następnego punktu na ścieżce.
     */
    public void update() {
        if (!alive) {
            return; // nic nie robimy, wróg już "martwy"
        }

        // Aktualizuj efekt spowolnienia (czy już się skończył?)
        long now = System.currentTimeMillis();
        if (now > slowEffectEndTime) {
            // Efekt spowolnienia minął – przywracamy bazową prędkość
            slowMultiplier = 1.0;
        }
        // Ustawiamy aktualną prędkość
        currentSpeed = baseSpeed * slowMultiplier;

        // Poruszaj się do kolejnego punktu (checkpoint) na ścieżce
        moveAlongPath();
    }

    /**
     * Rysowanie wroga: owal + pasek zdrowia.
     */
    public void draw(Graphics g) {
        if (!alive) {
            return;
        }

        // Rysujemy ciało wroga
        g.setColor(Color.RED);
        g.fillOval((int) (x - size / 2), (int) (y - size / 2), size, size);

        // Pasek HP nad wrogiem (np. ma szerokość równą rozmiarowi wroga)
        g.setColor(Color.BLACK);
        int barWidth = size;
        int barHeight = 4;
        int currentBarWidth = (int) ((health / (double) maxHealth) * barWidth);

        // Tło paska
        g.fillRect((int) (x - size / 2), (int) (y - size / 2) - 8, barWidth, barHeight);
        // Kolor "zielony" paska
        g.setColor(Color.GREEN);
        g.fillRect((int) (x - size / 2), (int) (y - size / 2) - 8, currentBarWidth, barHeight);
    }

    /**
     * Otrzymywanie obrażeń. Jeżeli HP spadnie do 0, wróg umiera.
     */
    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) {
            alive = false;
        }
    }

    /**
     * Nałożenie efektu spowolnienia (np. przez wieżę "zamrażającą").
     * @param multiplier np. 0.5 => prędkość spada do 50%
     * @param durationMs czas trwania efektu w milisekundach
     */
    public void applySlow(double multiplier, long durationMs) {
        long now = System.currentTimeMillis();
        // Ustawiamy spowolnienie tylko wtedy, kiedy jest mocniejsze
        // lub przedłuża dotychczasowy efekt
        if (multiplier < slowMultiplier) {
            slowMultiplier = multiplier;
        }
        // Jeżeli nowy efekt trwa dłużej niż dotychczasowy, wydłuż czas
        long newEndTime = now + durationMs;
        if (newEndTime > slowEffectEndTime) {
            slowEffectEndTime = newEndTime;
        }
    }

    /**
     * Poruszanie się w kierunku aktualnego checkpointu.
     * Gdy osiągniemy dany punkt, przechodzimy do następnego.
     * Jeśli wyszliśmy poza listę punktów, wróg może "opuścić mapę"
     * (np. zadać obrażenia graczowi i zniknąć – zależy od logiki).
     */
    private void moveAlongPath() {
        if (path == null || path.isEmpty() || currentCheckpointIndex >= path.size()) {
            // Wróg dotarł do końca ścieżki –
            // np. uznajmy, że zniknął z mapy (alive = false),
            // albo w realnych TowerDefense – zmniejsza HP bazy gracza.
            alive = false;
            return;
        }

        Point target = path.get(currentCheckpointIndex);
        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 1.0) {
            // Doszliśmy do kolejnego punktu – przechodzimy do następnego
            currentCheckpointIndex++;
        } else {
            // Idziemy w kierunku kolejnego punktu
            double stepX = (dx / dist) * currentSpeed;
            double stepY = (dy / dist) * currentSpeed;
            x += stepX;
            y += stepY;
        }
    }

    /**
     * Sprawdza, czy wróg wciąż żyje.
     */
    public boolean isAlive() {
        return alive;
    }

    // Gettery
    public int getHealth() {
        return health;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
