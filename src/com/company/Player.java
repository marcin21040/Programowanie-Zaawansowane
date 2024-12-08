package com.company;

public class Player extends Character {
    private int experience;
    private int level;

    public Player(String name, int health, int attack, int defense) {
        super(name, health, attack, defense);
        this.experience = 0;
        this.level = 1;
    }

    @Override
    public void attack(Character target) {
        System.out.println(name + " attacks " + target.getName());
        target.takeDamage(attack);
    }

    public void gainExperience(int exp) {
        experience += exp;
        if (experience >= 100) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        experience = 0;
        attack += 5;
        health += 10;
        System.out.println(name + " leveled up to level " + level + "!");
    }
}
