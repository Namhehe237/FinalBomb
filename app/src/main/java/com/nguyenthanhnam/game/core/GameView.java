package com.nguyenthanhnam.game.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nguyenthanhnam.game.R;
import com.nguyenthanhnam.game.config.GameConfig;
import com.nguyenthanhnam.game.controller.*;
import com.nguyenthanhnam.game.entity.*;
import com.nguyenthanhnam.game.utils.TimeFormatter;

public class GameView extends View {
    private float cellSize;
    private Player player;
    private List<Wall> walls;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private Trophy trophy;
    private Paint paint;
    private Handler gameLoop;
    private Random random;
    private boolean isGameRunning;
    private GameCallback gameCallback;
    private GameController gameController;
    private InputController inputController;

    // Timer variables
    private long gameStartTime;
    private long currentTime;
    private boolean isTimerRunning;

    // Bitmaps
    private Bitmap playerBitmap;
    private Bitmap bombBitmap;
    private Bitmap wallBitmap;
    private Bitmap breakableWallBitmap;
    private Bitmap explosionBitmap;
    private Bitmap trophyBitmap;

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

        loadBitmaps();
        initializeGame();
        startGameLoop();
    }

    private void initializeGame() {
        player = new Player(1, 1);
        walls.clear();
        bombs.clear();
        explosions.clear();
        createWalls();
        placeTrophyBehindWall();

        gameController = new GameController(this, player, bombs, explosions, walls, trophy);
        inputController = new InputController(gameController, cellSize);

        isGameRunning = true;
        gameStartTime = System.currentTimeMillis();
        isTimerRunning = true;
        currentTime = 0;
    }

    private void loadBitmaps() {
        playerBitmap = getBitmapFromResource(R.drawable.player);
        bombBitmap = getBitmapFromResource(R.drawable.bomb);
        wallBitmap = getBitmapFromResource(R.drawable.wall);
        breakableWallBitmap = getBitmapFromResource(R.drawable.breakable_wall);
        explosionBitmap = getBitmapFromResource(R.drawable.explosion);
        trophyBitmap = getBitmapFromResource(R.drawable.trophy);
    }

    private Bitmap getBitmapFromResource(int resourceId) {
        return BitmapFactory.decodeResource(getResources(), resourceId);
    }

    private void createWalls() {
        for (int y = 0; y < GameConfig.GRID_SIZE; y++) {
            for (int x = 0; x < GameConfig.GRID_SIZE; x++) {
                if (x == 0 || x == GameConfig.GRID_SIZE - 1 || y == 0 || y == GameConfig.GRID_SIZE - 1) {
                    walls.add(new Wall(x, y, false));
                } else if (!(x == 1 && y == 1) && random.nextFloat() < GameConfig.WALL_PROBABILITY) {
                    walls.add(new Wall(x, y, true));
                }
            }
        }
    }

    private void placeTrophyBehindWall() {
        List<Wall> breakableWalls = new ArrayList<>();
        for (Wall wall : walls) {
            if (wall.isBreakable() &&
                    wall.getX() > 0 && wall.getX() < GameConfig.GRID_SIZE-1 &&
                    wall.getY() > 0 && wall.getY() < GameConfig.GRID_SIZE-1) {
                breakableWalls.add(wall);
            }
        }

        if (!breakableWalls.isEmpty()) {
            Wall selectedWall = breakableWalls.get(random.nextInt(breakableWalls.size()));
            trophy = new Trophy(selectedWall.getX(), selectedWall.getY());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / GameConfig.GRID_SIZE;

        // Scale bitmaps
        if (playerBitmap != null)
            playerBitmap = Bitmap.createScaledBitmap(playerBitmap, (int)cellSize, (int)cellSize, true);
        if (bombBitmap != null)
            bombBitmap = Bitmap.createScaledBitmap(bombBitmap, (int)cellSize, (int)cellSize, true);
        if (wallBitmap != null)
            wallBitmap = Bitmap.createScaledBitmap(wallBitmap, (int)cellSize, (int)cellSize, true);
        if (breakableWallBitmap != null)
            breakableWallBitmap = Bitmap.createScaledBitmap(breakableWallBitmap, (int)cellSize, (int)cellSize, true);
        if (explosionBitmap != null)
            explosionBitmap = Bitmap.createScaledBitmap(explosionBitmap, (int)cellSize, (int)cellSize, true);
        if (trophyBitmap != null)
            trophyBitmap = Bitmap.createScaledBitmap(trophyBitmap, (int)cellSize, (int)cellSize, true);
    }

    private void startGameLoop() {
        gameLoop.post(new Runnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    updateGame();
                    invalidate();
                    gameLoop.postDelayed(this, GameConfig.FRAME_TIME);
                }
            }
        });
    }

    private void updateGame() {
        gameController.updateGame();

        if (isTimerRunning) {
            currentTime = System.currentTimeMillis() - gameStartTime;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(150, 200, 150));

        // Draw walls
        for (Wall wall : walls) {
            Bitmap wallBitmap = wall.isBreakable() ? breakableWallBitmap : this.wallBitmap;
            if (wallBitmap != null) {
                canvas.drawBitmap(wallBitmap,
                        wall.getX() * cellSize,
                        wall.getY() * cellSize,
                        paint);
            }
        }

        // Draw trophy if visible
        if (trophy != null && trophyBitmap != null) {
            boolean trophyWallExists = false;
            for (Wall wall : walls) {
                if (wall.getX() == trophy.getX() && wall.getY() == trophy.getY()) {
                    trophyWallExists = true;
                    break;
                }
            }

            if (!trophyWallExists) {
                canvas.drawBitmap(trophyBitmap,
                        trophy.getX() * cellSize,
                        trophy.getY() * cellSize,
                        paint);
            }
        }

        // Draw bombs
        for (Bomb bomb : bombs) {
            if (bombBitmap != null) {
                canvas.drawBitmap(bombBitmap,
                        bomb.getX() * cellSize,
                        bomb.getY() * cellSize,
                        paint);
            }
        }

        // Draw explosions
        for (Explosion explosion : explosions) {
            if (explosionBitmap != null) {
                canvas.drawBitmap(explosionBitmap,
                        explosion.getX() * cellSize,
                        explosion.getY() * cellSize,
                        paint);
            }
        }

        // Draw player
        if (playerBitmap != null) {
            canvas.drawBitmap(playerBitmap,
                    player.getX() * cellSize,
                    player.getY() * cellSize,
                    paint);
        }

        // Draw HUD
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText("Lives: " + player.getLives(), 50, 50, paint);
        canvas.drawText("Time: " + TimeFormatter.formatTime(currentTime), 50, 100, paint);
    }

    public void handleDirectionInput(Direction direction) {
        if (isGameRunning) {
            gameController.handleMovement(direction);
        }
    }

    public void placeBomb() {
        if (isGameRunning) {
            gameController.placeBomb();
        }
    }

    public void endGame() {
        isGameRunning = false;
        isTimerRunning = false;
        if (gameCallback != null) {
            gameCallback.onGameOver();
        }
    }

    public void winGame() {
        isGameRunning = false;
        isTimerRunning = false;
        if (gameCallback != null) {
            // Sửa lại cách gọi callback, truyền cả 2 tham số
            gameCallback.onGameWon(
                    TimeFormatter.formatTime(currentTime),
                    currentTime
            );
        }
    }

    public void setGameCallback(GameCallback callback) {
        this.gameCallback = callback;
    }

    public void restartGame() {
        initializeGame();
        invalidate();
    }
}