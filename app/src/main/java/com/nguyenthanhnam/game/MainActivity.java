package com.nguyenthanhnam.game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GameView.GameCallback {
    private GameView gameView;
    private Button btnPlayAgain;
    private FrameLayout gameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view first
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // Setup button click listener
        setupPlayAgainButton();
    }

    private void initializeViews() {
        gameContainer = findViewById(R.id.gameContainer);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);

        // Create and add GameView
        gameView = new GameView(this);
        gameView.setGameCallback(this);
        gameContainer.addView(gameView);
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