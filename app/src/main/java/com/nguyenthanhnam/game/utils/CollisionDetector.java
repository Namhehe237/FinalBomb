package com.nguyenthanhnam.game.utils;

import com.nguyenthanhnam.game.entity.*;
import java.util.List;

public class CollisionDetector {
    public static boolean canMoveTo(float x, float y, List<Wall> walls, List<Bomb> bombs) {
        // Check wall collisions
        for (Wall wall : walls) {
            if (wall.getX() == x && wall.getY() == y) {
                return false;
            }
        }

        // Check bomb collisions
        for (Bomb bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                return false;
            }
        }

        return true;
    }
}