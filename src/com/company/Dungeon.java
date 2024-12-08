package com.company;

import java.util.Random;

public class Dungeon {
    private Tile[][] map;

    public Dungeon(int width, int height) {
        map = new Tile[width][height];
        generateDungeon();
    }

    private void generateDungeon() {
        Random rand = new Random();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (rand.nextInt(100) < 10) {
                    map[i][j] = new Tile("wall");
                } else {
                    map[i][j] = new Tile("empty");
                }
            }
        }
    }

    public Tile getTile(int x, int y) {
        return map[x][y];
    }

    public void printDungeon() {
        for (Tile[] row : map) {
            for (Tile tile : row) {
                System.out.print(tile.getType().charAt(0) + " ");
            }
            System.out.println();
        }
    }
}
