package com.nguyenthanhnam.game.entity;

import java.util.Random;
import com.nguyenthanhnam.game.controller.Direction;

public class Enemy extends GameObject {
    private boolean isAlive;
    private float speed;
    private Random random;

    public Enemy(float x, float y) {
        super(x, y);
        this.isAlive = true;
        this.speed = 1.0f;
        this.random = new Random();
    }

    public Direction getRandomDirection() {
        int rand = random.nextInt(4);
        switch(rand) {
            case 0: return Direction.UP;
            case 1: return Direction.DOWN;
            case 2: return Direction.LEFT;
            default: return Direction.RIGHT;
        }
    }

    public float getNextX(Direction direction) {
        switch(direction) {
            case LEFT: return x - speed;
            case RIGHT: return x + speed;
            default: return x;
        }
    }

    public float getNextY(Direction direction) {
        switch(direction) {
            case UP: return y - speed;
            case DOWN: return y + speed;
            default: return y;
        }
    }

    public boolean isAlive() { return isAlive; }
    public void kill() { isAlive = false; }
}