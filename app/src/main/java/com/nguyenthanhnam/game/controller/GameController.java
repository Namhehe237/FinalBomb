package com.nguyenthanhnam.game.controller;

import com.nguyenthanhnam.game.config.GameConfig;
import com.nguyenthanhnam.game.core.GameView;
import com.nguyenthanhnam.game.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

public class GameController {
    private GameView gameView;
    private CollisionController collisionController;
    private Player player;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private List<Wall> walls;
    private Trophy trophy;
    private List<BombUpItem> bombUpItems;
    private List<FireUpItem> fireUpItems;
    private List<Enemy> enemies;


    public GameController(GameView gameView, Player player, List<Bomb> bombs,
                          List<Explosion> explosions, List<Wall> walls, Trophy trophy) {
        this.gameView = gameView;
        this.player = player;
        this.bombs = bombs;
        this.explosions = explosions;
        this.walls = walls;
        this.trophy = trophy;
        this.collisionController = new CollisionController(walls, bombs);
        this.bombUpItems = new ArrayList<>();
        this.fireUpItems = new ArrayList<>();
        this.enemies = new ArrayList<>();
        spawnEnemies();
        placeBombUpItem();
        placeFireUpItem();
    }

    private void spawnEnemies() {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(GameConfig.GRID_SIZE - 2) + 1;
            int y = random.nextInt(GameConfig.GRID_SIZE - 2) + 1;
            enemies.add(new Enemy(x, y));
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                // Kiểm tra va chạm với explosion
                for (Explosion explosion : explosions) {
                    if (explosion.getX() == enemy.getX() && explosion.getY() == enemy.getY()) {
                        enemy.kill();
                        break;
                    }
                }

                // Di chuyển enemy
                if (enemy.isAlive() && Math.random() < 0.1) {
                    Direction direction = enemy.getRandomDirection();
                    float newX = enemy.getNextX(direction);
                    float newY = enemy.getNextY(direction);
                    if (canEnemyMoveTo(newX, newY)) {
                        enemy.setX(newX);
                        enemy.setY(newY);
                    }
                }
            }
        }
        checkEnemyPlayerCollision();
    }

    private boolean canEnemyMoveTo(float x, float y) {
        // Kiểm tra giới hạn map
        if (x < 0 || x >= GameConfig.GRID_SIZE || y < 0 || y >= GameConfig.GRID_SIZE) {
            return false;
        }

        // Kiểm tra va chạm với tường
        for (Wall wall : walls) {
            if (wall.getX() == x && wall.getY() == y) {
                return false;
            }
        }

        // Kiểm tra va chạm với bomb
        for (Bomb bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                return false;
            }
        }
        return true;
    }

    private void checkEnemyPlayerCollision() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() &&
                    enemy.getX() == player.getX() &&
                    enemy.getY() == player.getY()) {
                player.decreaseLives();
                break;
            }
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }



    private void placeFireUpItem() {
        List<Wall> breakableWalls = new ArrayList<>();
        for (Wall wall : walls) {
            if (wall.isBreakable()) {
                breakableWalls.add(wall);
            }
        }

        if (!breakableWalls.isEmpty()) {
            Wall selectedWall = breakableWalls.get(new Random().nextInt(breakableWalls.size()));
            fireUpItems.add(new FireUpItem(selectedWall.getX(), selectedWall.getY()));
        }
    }

    private void placeBombUpItem() {
        List<Wall> breakableWalls = new ArrayList<>();
        for (Wall wall : walls) {
            if (wall.isBreakable()) {
                breakableWalls.add(wall);
            }
        }

        if (!breakableWalls.isEmpty()) {
            Wall selectedWall = breakableWalls.get(new Random().nextInt(breakableWalls.size()));
            bombUpItems.add(new BombUpItem(selectedWall.getX(), selectedWall.getY()));
        }
    }

    public void updateGame() {
        updateBombs();
        updateExplosions();
        updateEnemies();
        checkCollisions();
        checkTrophyCollision();
        checkBombUpCollision();
        checkFireUpCollision();
    }

    private void checkFireUpCollision() {
        Iterator<FireUpItem> iterator = fireUpItems.iterator();
        while (iterator.hasNext()) {
            FireUpItem item = iterator.next();
            if (!item.isCollected()) {
                boolean hiddenByWall = false;
                for (Wall wall : walls) {
                    if (wall.getX() == item.getX() && wall.getY() == item.getY()) {
                        hiddenByWall = true;
                        break;
                    }
                }

                if (!hiddenByWall && player.getX() == item.getX() && player.getY() == item.getY()) {
                    player.increaseExplosionPower(); // Tăng sức mạnh nổ
                    item.collect();
                    iterator.remove();
                }
            }
        }
    }


    private void checkBombUpCollision() {
        Iterator<BombUpItem> iterator = bombUpItems.iterator();
        while (iterator.hasNext()) {
            BombUpItem item = iterator.next();
            if (!item.isCollected()) {
                boolean hiddenByWall = false;
                for (Wall wall : walls) {
                    if (wall.getX() == item.getX() && wall.getY() == item.getY()) {
                        hiddenByWall = true;
                        break;
                    }
                }

                if (!hiddenByWall && player.getX() == item.getX() && player.getY() == item.getY()) {
                    player.increaseBombCapacity();
                    item.collect();
                    iterator.remove();
                }
            }
        }
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



    public void placeBomb(){
        if (bombs.size() < player.getBombCapacity()){
            Bomb bomb = new Bomb(player.getX(), player.getY());
            bomb.setExplosionRange(player.getExplosionPower()); // Thêm dòng này
            bombs.add(bomb);
            gameView.invalidate();
        }
    }



    private void createExplosion(Bomb bomb){
        explosions.add(new Explosion(bomb.getX(), bomb.getY()));

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] direction : directions){
            for (int i=1 ;i<= bomb.getExplosionRange();i++){
                float newX = bomb.getX() + direction[0]*i;
                float newY = bomb.getY() + direction[1]*i;

                if (handleExplosionAt(newX,newY)) break;
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


    private void checkTrophyCollision(){
        if (trophy!=null) {
            boolean trophyWall = false;
            for (Wall wall : walls) {
                if (wall.getX() == trophy.getX() && wall.getY() == trophy.getY()) {
                    trophyWall = true;
                    break;
                }
            }
            if (!trophyWall && player.getX()== trophy.getX() && player.getY()==trophy.getY()){
                gameView.winGame();
            }
        }
    }
    public List<BombUpItem> getBombUpItems() {
        return bombUpItems;
    }

    public List<FireUpItem> getFireUpItems() {
        return fireUpItems;
    }
}