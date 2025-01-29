package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sztywna plansza z jedną poziomą ścieżką w wierszu 1.
 * 0 = ścieżka
 * 1 = blokada (teren na wieże)
 */
public class MichalBoard {

    // Wymiary siatki (6 wierszy, 7 kolumn)
    private final int rows = 9;
    private final int cols = 10;

    /**
     * Tablica kafelków:
     *   - w wierszu 1 mamy ścieżkę od kolumny 0 do 6,
     *   - reszta to blokady.
     */
    private int[][] tiles = {
            {1,1,1,1,1,1,1,1,1,1},  // row=0
            {0,0,0,0,0,1,1,1,1,1},  // row=1 (jedyna ścieżka)
            {1,1,1,1,0,1,1,1,1,1},  // row=2
            {1,1,1,1,0,1,1,1,1,1},  // row=3
            {1,1,1,1,0,0,0,1,1,1},  // row=4
            {1,1,1,1,1,1,0,1,1,1},   // row=5
            {1,1,1,1,1,1,0,1,1,1},   // row=6
            {1,1,1,1,1,1,0,1,1,1},   // row=7
            {1,1,1,1,1,1,0,1,1,1},   // row=8
    };

    /**
     * Zwraca listę punktów (waypointów) – tu będzie pozioma linia w row=1,
     * kolumny od 0 do 6 (7 kolumn).
     */
    public List<Point> getWaypoints() {
        List<Point> path = new ArrayList<>();
        // Dla każdej kolumny szukamy wiersza z 0
        // (zakładamy, że w danej kolumnie jest tylko 1 ścieżka).
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (tiles[r][c] == 0) {
                    // Dodajemy (c, r)
                    // Uwaga: to kolejność (kolumna, wiersz),
                    // w pikselach często przeliczamy x=c, y=r
                    path.add(new Point(c, r));
                    break;
                }
            }
        }
        return path;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /**
     * Zwraca kafelek (0 albo 1) dla wiersza i kolumny.
     */
    public int getTile(int row, int col) {
        return tiles[row][col];
    }
}
