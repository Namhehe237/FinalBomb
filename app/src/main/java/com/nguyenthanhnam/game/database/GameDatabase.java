// GameDatabase.java
package com.nguyenthanhnam.game.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nguyenthanhnam.game.database.dao.GameTimeDao;
import com.nguyenthanhnam.game.database.entity.GameTime;

@Database(entities = {GameTime.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {
    private static GameDatabase instance;
    public abstract GameTimeDao gameTimeDao();

    public static synchronized GameDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    GameDatabase.class,
                    "game_database"
            ).build();
        }
        return instance;
    }
}