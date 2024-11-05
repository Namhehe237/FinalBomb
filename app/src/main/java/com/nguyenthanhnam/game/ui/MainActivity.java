package com.nguyenthanhnam.game.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenthanhnam.game.R;
import com.nguyenthanhnam.game.core.GameView;
import com.nguyenthanhnam.game.core.GameCallback;
import com.nguyenthanhnam.game.controller.Direction;

public class MainActivity extends AppCompatActivity implements GameCallback {
    private GameView gameView;
    private Button btnPlayAgain;
    private Button btnUp, btnDown, btnLeft, btnRight, btnBomb;
    private FrameLayout gameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupControlButtons();
        setupPlayAgainButton();
    }

    private void initializeViews() {
        gameContainer = findViewById(R.id.gameContainer);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnBomb = findViewById(R.id.btnBomb);

        gameView = new GameView(this);
        gameView.setGameCallback(this);
        gameContainer.addView(gameView);
    }

    private void setupControlButtons() {
        View.OnClickListener directionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnUp) {
                    gameView.handleDirectionInput(Direction.UP);
                } else if (v == btnDown) {
                    gameView.handleDirectionInput(Direction.DOWN);
                } else if (v == btnLeft) {
                    gameView.handleDirectionInput(Direction.LEFT);
                } else if (v == btnRight) {
                    gameView.handleDirectionInput(Direction.RIGHT);
                }
            }
        };

        btnUp.setOnClickListener(directionListener);
        btnDown.setOnClickListener(directionListener);
        btnLeft.setOnClickListener(directionListener);
        btnRight.setOnClickListener(directionListener);

        btnBomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.placeBomb();
            }
        });
    }

    private void setupPlayAgainButton() {
        btnPlayAgain.setVisibility(View.GONE);
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlayAgain.setVisibility(View.GONE);
                btnPlayAgain.setText("Play Again");
                gameView.restartGame();
            }
        });
    }

    @Override
    public void onGameOver() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPlayAgain.setText("Game Over - Play Again");
                btnPlayAgain.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onGameWon(final String finalTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPlayAgain.setText("You Won! Time: " + finalTime + "\nPlay Again");
                btnPlayAgain.setVisibility(View.VISIBLE);
            }
        });
    }
}