package com.nguyenthanhnam.game.controller;

import com.nguyenthanhnam.game.entity.*;
import com.nguyenthanhnam.game.utils.CollisionDetector;
import java.util.List;

public class CollisionController {
    private List<Wall> walls;
    private List<Bomb> bombs;

    public CollisionController(List<Wall> walls, List<Bomb> bombs) {
        this.walls = walls;
        this.bombs = bombs;
    }

    public boolean canMoveTo(float x, float y) {
        return CollisionDetector.canMoveTo(x, y, walls, bombs);
    }

    public void checkPlayerCollisions(Player player, List<Explosion> explosions) {
        for (Explosion explosion : explosions) {
            if (player.getX() == explosion.getX() && player.getY() == explosion.getY()) {
                player.decreaseLives();
                return;
            }
        }
    }
}