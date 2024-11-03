package com.nguyenthanhnam.game;


public class Wall extends GameObject {
    private boolean breakable;

    public Wall(float x, float y, boolean breakable) {
        super(x, y);
        this.breakable = breakable;
    }

    public boolean isBreakable() {
        return breakable;
    }
}
