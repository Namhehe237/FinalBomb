package com.nguyenthanhnam.game;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GameView.GameCallback {
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
        // Direction buttons
        View.OnTouchListener directionListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (v == btnUp) {
                        gameView.movePlayer(0, -1);
                    } else if (v == btnDown) {
                        gameView.movePlayer(0, 1);
                    } else if (v == btnLeft) {
                        gameView.movePlayer(-1, 0);
                    } else if (v == btnRight) {
                        gameView.movePlayer(1, 0);
                    }
                    return true;
                }
                return false;
            }
        };

        btnUp.setOnTouchListener(directionListener);
        btnDown.setOnTouchListener(directionListener);
        btnLeft.setOnTouchListener(directionListener);
        btnRight.setOnTouchListener(directionListener);

        // Bomb button
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
                gameView.restartGame();
            }
        });
    }

    @Override
    public void onGameOver() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPlayAgain.setVisibility(View.VISIBLE);
            }
        });
    }
}