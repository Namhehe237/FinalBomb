// GameTimeRepository.java
package com.nguyenthanhnam.game.database;

import android.content.Context;
import android.os.AsyncTask;
import com.nguyenthanhnam.game.database.entity.GameTime;
import com.nguyenthanhnam.game.database.dao.GameTimeDao;

public class GameTimeRepository {
    private GameTimeDao gameTimeDao;

    public GameTimeRepository(Context context) {
        GameDatabase database = GameDatabase.getInstance(context);
        gameTimeDao = database.gameTimeDao();
    }

    public void insert(GameTime gameTime) {
        new InsertGameTimeAsyncTask(gameTimeDao).execute(gameTime);
    }

    private static class InsertGameTimeAsyncTask extends AsyncTask<GameTime, Void, Void> {
        private GameTimeDao gameTimeDao;

        private InsertGameTimeAsyncTask(GameTimeDao gameTimeDao) {
            this.gameTimeDao = gameTimeDao;
        }

        @Override
        protected Void doInBackground(GameTime... gameTimes) {
            gameTimeDao.insert(gameTimes[0]);
            return null;
        }
    }
}