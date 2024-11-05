package com.nguyenthanhnam.game.core;

public interface GameCallback {
    void onGameOver();
    void onGameWon(String finalTime);
}