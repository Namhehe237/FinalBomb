package com.nguyenthanhnam.game.entity;


public class Player extends GameObject {
    private int lives;
    private int bombCapacity;
    private int explosionPower;

    public Player(float x, float y) {
        super(x, y);
        this.lives = 3;
        this.bombCapacity = 1;
        this.explosionPower = 1;
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
    public void increaseExplosionPower() {
        explosionPower++;
    }

    public int getExplosionPower() {
        return explosionPower;
    }
}
