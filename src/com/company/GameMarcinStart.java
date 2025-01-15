package com.company;

public class GameMarcinStart {

    public void startGame() {
        Game game = new GameWordle(6); // 6 prób na zgadnięcie
        game.startGame();
    }
}
