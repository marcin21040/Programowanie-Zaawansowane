package com.company;

public class Goblin extends Enemy {
    public Goblin() {
        super("Goblin", 30, 10, 2);
    }

    @Override
    public void attack(Character target) {
        System.out.println(name + " slashes " + target.getName());
        target.takeDamage(attack);
    }
}
