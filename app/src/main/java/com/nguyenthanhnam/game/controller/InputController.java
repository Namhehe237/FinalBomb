package com.nguyenthanhnam.game.controller;

import android.view.MotionEvent;
import com.nguyenthanhnam.game.core.GameView;
import com.nguyenthanhnam.game.config.GameConfig;

public class InputController {
    private GameController gameController;
    private float cellSize;

    public InputController(GameController gameController, float cellSize) {
        this.gameController = gameController;
        this.cellSize = cellSize;
    }

    public void handleDirectionButtonClick(Direction direction) {
        gameController.handleMovement(direction);
    }

    public void handleBombButtonClick() {
        gameController.placeBomb();
    }
}