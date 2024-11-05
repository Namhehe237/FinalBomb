package com.nguyenthanhnam.game.entity;


public class Explosion extends GameObject {
    private int timer;

    public Explosion(float x, float y) {
        super(x, y);
        this.timer = 5; // 0.5 seconds at 10 fps
    }

    public void tick() {
        timer--;
    }

    public boolean shouldEnd() {
        return timer <= 0;
    }
}
