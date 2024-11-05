package com.nguyenthanhnam.game.entity;


public class Player extends GameObject {
    private int lives;
    private int bombCapacity;

    public Player(float x, float y) {
        super(x, y);
        this.lives = 3;
        this.bombCapacity = 1;
    }

    public int getLives() {
        return lives;
    }

    public void decreaseLives() {
        lives--;
    }

    public int getBombCapacity() {
        return bombCapacity;
    }

    public void increaseBombCapacity() {
        bombCapacity++;
    }
}
