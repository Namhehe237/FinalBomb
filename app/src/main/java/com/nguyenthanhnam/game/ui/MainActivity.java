package com.nguyenthanhnam.game.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.nguyenthanhnam.game.R;
import com.nguyenthanhnam.game.core.GameView;
import com.nguyenthanhnam.game.core.GameCallback;
import com.nguyenthanhnam.game.controller.Direction;
import com.nguyenthanhnam.game.database.GameTimeRepository;
import com.nguyenthanhnam.game.database.entity.GameTime;

public class MainActivity extends AppCompatActivity implements GameCallback {
    private GameView gameView;
    private Button btnPlayAgain;
    private Button btnUp, btnDown, btnLeft, btnRight, btnBomb;
    private FrameLayout gameContainer;
    private GameTimeRepository gameTimeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameTimeRepository = new GameTimeRepository(this);
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

//    @Override
//    public void onGameWon(final String finalTime) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                btnPlayAgain.setText("You Won! Time: " + finalTime + "\nPlay Again");
//                btnPlayAgain.setVisibility(View.VISIBLE);
//            }
//        });
//    }

//    @Override
//    public void onGameWon(final String finalTime, final long timeInMillis) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                btnPlayAgain.setText("You Won! Time: " + finalTime + "\nPlay Again");
//                btnPlayAgain.setVisibility(View.VISIBLE);
//                // Lưu thời gian vào database
//                GameTime gameTime = new GameTime(timeInMillis, finalTime);
//                gameTimeRepository.insert(gameTime);
//            }
//        });
//    }
//    @Override
//    public void onGameWon(final String finalTime, final long timeInMillis) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showNameInputDialog(finalTime, timeInMillis);
//            }
//        });
//    }

    @Override
    public void onGameWon(String finalTime, long timeInMillis) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNameInputDialog(finalTime, timeInMillis);
            }
        });
    }

    private void showNameInputDialog(final String finalTime, final long timeInMillis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Victory!");
        builder.setMessage("Enter your name:");

        // Tạo EditText để nhập tên
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                if (playerName.isEmpty()) {
                    playerName = "Anonymous";
                }

                // Lưu vào database
                GameTime gameTime = new GameTime(playerName, timeInMillis, finalTime);
                gameTimeRepository.insert(gameTime);

                // Hiển thị nút chơi lại
                btnPlayAgain.setText("You Won! " + playerName + "\nTime: " + finalTime + "\nPlay Again");
                btnPlayAgain.setVisibility(View.VISIBLE);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nếu không nhập tên, lưu là Anonymous
                GameTime gameTime = new GameTime("Anonymous", timeInMillis, finalTime);
                gameTimeRepository.insert(gameTime);

                btnPlayAgain.setText("You Won!\nTime: " + finalTime + "\nPlay Again");
                btnPlayAgain.setVisibility(View.VISIBLE);
                dialog.cancel();
            }
        });

        builder.show();
    }

}