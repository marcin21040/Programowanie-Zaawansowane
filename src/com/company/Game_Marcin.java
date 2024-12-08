package com.company;

public class Game_Marcin {
    private Player player;
    private Dungeon dungeon;

    public Game_Marcin() {
        player = new Player("Hero", 100, 20, 5);
        dungeon = new Dungeon(10, 10);
    }

    public void start() {
        dungeon.printDungeon();
        Enemy goblin = new Goblin();
        player.attack(goblin);
        goblin.attack(player);
    }
}
