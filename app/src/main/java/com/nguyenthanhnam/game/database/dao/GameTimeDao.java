// GameTimeDao.java
package com.nguyenthanhnam.game.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import com.nguyenthanhnam.game.database.entity.GameTime;

@Dao
public interface GameTimeDao {
    @Insert
    void insert(GameTime gameTime);
}