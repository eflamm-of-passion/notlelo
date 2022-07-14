package io.eflamm.notlelo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import io.eflamm.notlelo.model.Picture

@Dao
interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(picture: Picture)
}