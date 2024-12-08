package com.company;

public abstract class Character {
    protected String name;
    protected int health;
    protected int attack;
    protected int defense;

    public Character(String name, int health, int attack, int defense) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
    }

    public void takeDamage(int damage) {
        int damageTaken = damage - defense;
        if (damageTaken > 0) {
            health -= damageTaken;
        }
        if (health <= 0) {
            System.out.println(name + " is dead.");
        }
    }

    public abstract void attack(Character target);

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }
}
