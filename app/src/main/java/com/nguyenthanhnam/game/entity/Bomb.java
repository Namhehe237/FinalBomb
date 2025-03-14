package com.nguyenthanhnam.game.entity;


public class Bomb extends GameObject {
    private int timer;
    private int explosionRange;

    public Bomb(float x, float y) {
        super(x, y);
        this.timer = 30; // 3 seconds at 10 fps
        this.explosionRange = 1;
    }

    public void tick() {
        timer--;
    }


    public void setExplosionRange(int range) {
        this.explosionRange = range;
    }

    public boolean shouldExplode() {
        return timer <= 0;
    }

    public void increaseExplosionRange(){
        explosionRange++;
    }

    public int getExplosionRange() {
        return explosionRange;
    }
}