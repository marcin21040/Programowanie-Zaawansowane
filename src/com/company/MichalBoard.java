package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sztywna plansza z poziomem gry.
 * 0 = ścieżka
 * 1 = blokada (teren na wieże)
 */
public class MichalBoard {

    private int rows;
    private int cols;
    private int[][] tiles;


    private static final int[][] LEVEL1 = {
            {1,1,1,1,1,1,1,1,1,1},
            {0,0,0,0,0,1,1,1,1,1},
            {1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,0,0,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
    };


    private static final int[][] LEVEL2 = {
            {1,1,1,1,1,1,1,1,1,1},
            {0,0,0,0,1,1,1,1,1,1},
            {0,0,0,0,1,1,1,1,1,1},
            {1,1,1,0,0,0,0,0,1,1},
            {1,1,1,1,1,1,0,0,1,1},
            {1,1,1,1,1,1,0,0,1,1},
            {1,1,1,1,1,1,1,0,1,1},
            {1,1,1,1,1,1,1,0,1,1},
            {1,1,1,1,1,1,1,0,1,1},
    };

    public MichalBoard(int level) {
        switch (level) {
            case 2:
                tiles = LEVEL2;
                break;
            default:
                tiles = LEVEL1;
                break;
        }
        rows = tiles.length;
        cols = tiles[0].length;
    }


    public List<Point> getWaypoints() {
        List<Point> path = new ArrayList<>();
        // Dla każdej kolumny szukamy wiersza z 0
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (tiles[r][c] == 0) {
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


    public int getTile(int row, int col) {
        return tiles[row][col];
    }
}
