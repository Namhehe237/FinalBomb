package com.nguyenthanhnam.game;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private static final int GRID_SIZE = 15;
    private static final int GAME_FPS = 10;
    private static final long FRAME_TIME = 1000 / GAME_FPS;

    private float cellSize;
    private Player player;
    private List<Wall> walls;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private Paint paint;
    private Handler gameLoop;
    private Random random;
    private boolean isGameRunning;

    public GameView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        paint = new Paint();
        random = new Random();
        gameLoop = new Handler(Looper.getMainLooper());
        walls = new ArrayList<>();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();

        initializeGame();
        startGameLoop();
    }

    private void initializeGame() {
        player = new Player(1, 1);
        createWalls();
        isGameRunning = true;
    }

    private void createWalls() {
        // Create border walls and random breakable walls
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (x == 0 || x == GRID_SIZE - 1 || y == 0 || y == GRID_SIZE - 1) {
                    walls.add(new Wall(x, y, false)); // Border walls
                } else if (!(x == 1 && y == 1) && random.nextFloat() < 0.3f) {
                    walls.add(new Wall(x, y, true)); // Breakable walls
                }
            }
        }
    }

    private void startGameLoop() {
        gameLoop.post(new Runnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    updateGame();
                    invalidate();
                    gameLoop.postDelayed(this, FRAME_TIME);
                }
            }
        });
    }

    private void updateGame() {
        updateBombs();
        updateExplosions();
        checkPlayerCollisions();
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

    private void createExplosion(Bomb bomb) {
        // Center explosion
        explosions.add(new Explosion(bomb.getX(), bomb.getY()));

        // Directional explosions
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] direction : directions) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                float newX = bomb.getX() + (direction[0] * i);
                float newY = bomb.getY() + (direction[1] * i);

                if (handleExplosionAt(newX, newY)) {
                    break; // Stop this direction if we hit a wall
                }
            }
        }
    }

    private boolean handleExplosionAt(float x, float y) {
        // Check for walls
        Iterator<Wall> wallIterator = walls.iterator();
        while (wallIterator.hasNext()) {
            Wall wall = wallIterator.next();
            if (wall.getX() == x && wall.getY() == y) {
                if (wall.isBreakable()) {
                    wallIterator.remove();
                }
                return true; // Explosion stops at wall
            }
        }

        explosions.add(new Explosion(x, y));
        return false;
    }

    private void checkPlayerCollisions() {
        // Check if player touches any explosion
        for (Explosion explosion : explosions) {
            if (player.getX() == explosion.getX() && player.getY() == explosion.getY()) {
                handlePlayerHit();
                break;
            }
        }
    }

    private void handlePlayerHit() {
        player.decreaseLives();
        if (player.getLives() <= 0) {
            isGameRunning = false;
            // Could add game over handling here
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / GRID_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawColor(Color.rgb(150, 200, 150)); // Light green background

        // Draw walls
        for (Wall wall : walls) {
            paint.setColor(wall.isBreakable() ? Color.rgb(139, 69, 19) : Color.GRAY);
            drawCell(canvas, wall.getX(), wall.getY());
        }

        // Draw bombs
        paint.setColor(Color.BLACK);
        for (Bomb bomb : bombs) {
            drawCircle(canvas, bomb.getX(), bomb.getY());
        }

        // Draw explosions
        paint.setColor(Color.RED);
        for (Explosion explosion : explosions) {
            drawCell(canvas, explosion.getX(), explosion.getY());
        }

        // Draw player
        paint.setColor(Color.BLUE);
        drawCircle(canvas, player.getX(), player.getY());
    }

    private void drawCell(Canvas canvas, float x, float y) {
        canvas.drawRect(
                x * cellSize,
                y * cellSize,
                (x + 1) * cellSize,
                (y + 1) * cellSize,
                paint
        );
    }

    private void drawCircle(Canvas canvas, float x, float y) {
        canvas.drawCircle(
                (x + 0.5f) * cellSize,
                (y + 0.5f) * cellSize,
                cellSize * 0.4f,
                paint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameRunning || event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }

        float touchX = event.getX() / cellSize;
        float touchY = event.getY() / cellSize;

        // Handle movement
        handlePlayerMovement(touchX, touchY);

        // Place bomb
        if (bombs.size() < player.getBombCapacity()) {
            bombs.add(new Bomb(player.getX(), player.getY()));
        }

        invalidate();
        return true;
    }

    private void handlePlayerMovement(float touchX, float touchY) {
        float dx = touchX - player.getX();
        float dy = touchY - player.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal movement
            float newX = player.getX() + Math.signum(dx);
            if (canMoveTo(newX, player.getY())) {
                player.setX(newX);
            }
        } else {
            // Vertical movement
            float newY = player.getY() + Math.signum(dy);
            if (canMoveTo(player.getX(), newY)) {
                player.setY(newY);
            }
        }
    }

    private boolean canMoveTo(float x, float y) {
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