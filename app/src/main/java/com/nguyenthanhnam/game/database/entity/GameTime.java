// GameTime.java (Entity)
package com.nguyenthanhnam.game.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_times")
public class GameTime {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String playerName;
    private long timeInMillis;
    private String formattedTime;


    public GameTime(String playerName, long timeInMillis, String formattedTime) {
        this.playerName = playerName;
        this.timeInMillis = timeInMillis;
        this.formattedTime = formattedTime;
    }

    // Getters and Setters

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getTimeInMillis() { return timeInMillis; }
    public String getFormattedTime() { return formattedTime; }
}