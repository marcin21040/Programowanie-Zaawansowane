package com.company;


public abstract class Game {
    protected int maxAttempts;

    public Game(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public abstract void startGame();
}
