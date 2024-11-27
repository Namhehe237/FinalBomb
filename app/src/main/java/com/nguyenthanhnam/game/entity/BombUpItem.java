package com.nguyenthanhnam.game.entity;

public class BombUpItem extends GameObject {
    private boolean isCollected;

    public BombUpItem(float x, float y) {
        super(x, y);
        this.isCollected = false;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        isCollected = true;
    }

}