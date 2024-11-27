package com.nguyenthanhnam.game.entity;

public class FireUpItem extends GameObject {
    private boolean isCollected;

    public FireUpItem(float x, float y) {
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