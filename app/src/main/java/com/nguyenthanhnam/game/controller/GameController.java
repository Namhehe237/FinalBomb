package com.nguyenthanhnam.game.controller;

import com.nguyenthanhnam.game.core.GameView;
import com.nguyenthanhnam.game.entity.*;
import java.util.List;
import java.util.Iterator;

public class GameController {
    private GameView gameView;
    private CollisionController collisionController;
    private Player player;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private List<Wall> walls;
    private Trophy trophy;

    public GameController(GameView gameView, Player player, List<Bomb> bombs,
                          List<Explosion> explosions, List<Wall> walls, Trophy trophy) {
        this.gameView = gameView;
        this.player = player;
        this.bombs = bombs;
        this.explosions = explosions;
        this.walls = walls;
        this.trophy = trophy;
        this.collisionController = new CollisionController(walls, bombs);
    }

    public void updateGame() {
        updateBombs();
        updateExplosions();
        checkCollisions();
        checkTrophyCollision();
    }

    private void updateBombs() {
        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            bomb.tick();
            if (bomb.shouldExplode()) {
                createExplosion(bomb);
                bombIterator.remove();
            }
        }
    }

    private void updateExplosions() {
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.tick();
            if (explosion.shouldEnd()) {
                explosionIterator.remove();
            }
        }
    }

    public void handleMovement(Direction direction) {
        float newX = player.getX();
        float newY = player.getY();

        switch (direction) {
            case UP: newY--; break;
            case DOWN: newY++; break;
            case LEFT: newX--; break;
            case RIGHT: newX++; break;
        }

        if (collisionController.canMoveTo(newX, newY)) {
            player.setX(newX);
            player.setY(newY);
            gameView.invalidate();
        }
    }

    public void placeBomb() {
        if (bombs.size() < player.getBombCapacity()) {
            bombs.add(new Bomb(player.getX(), player.getY()));
            gameView.invalidate();
        }
    }

    private void createExplosion(Bomb bomb) {
        explosions.add(new Explosion(bomb.getX(), bomb.getY()));

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] direction : directions) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                float newX = bomb.getX() + (direction[0] * i);
                float newY = bomb.getY() + (direction[1] * i);

                if (handleExplosionAt(newX, newY)) {
                    break;
                }
            }
        }
    }

    private boolean handleExplosionAt(float x, float y) {
        Iterator<Wall> wallIterator = walls.iterator();
        while (wallIterator.hasNext()) {
            Wall wall = wallIterator.next();
            if (wall.getX() == x && wall.getY() == y) {
                if (wall.isBreakable()) {
                    wallIterator.remove();
                }
                return true;
            }
        }

        explosions.add(new Explosion(x, y));
        return false;
    }

    private void checkCollisions() {
        collisionController.checkPlayerCollisions(player, explosions);
        if (player.getLives() <= 0) {
            gameView.endGame();
        }
    }

    private void checkTrophyCollision() {
        if (trophy != null) {
            boolean trophyWallExists = false;
            for (Wall wall : walls) {
                if (wall.getX() == trophy.getX() && wall.getY() == trophy.getY()) {
                    trophyWallExists = true;
                    break;
                }
            }

            if (!trophyWallExists &&
                    player.getX() == trophy.getX() &&
                    player.getY() == trophy.getY()) {
                gameView.winGame();
            }
        }
    }
}