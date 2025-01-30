package com.company;

public enum TowerType {
    BASIC(100, 25, 1000, 50),
    SNIPER(200, 50, 2000, 100);

    private int range;
    private int damage;
    private int cooldown;
    private int cost;

    TowerType(int range, int damage, int cooldown, int cost) {
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.cost = cost;
    }

    public int getRange() {
        return range;
    }

    public int getDamage() {
        return damage;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCost() {
        return cost;
    }
}
