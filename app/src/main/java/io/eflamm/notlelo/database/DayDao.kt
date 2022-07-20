package io.eflamm.notlelo.database

import androidx.room.*
import io.eflamm.notlelo.model.Day
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DayDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(date: Day): Long

    @Transaction
    @Query("SELECT * FROM Day WHERE event_id = :eventId AND date = :date")
    fun getDayByEventIdByDate(eventId: Long, date: LocalDate): Flow<Day?>

    @Query("DELETE FROM Day WHERE id NOT IN (SELECT DISTINCT day_id FROM Meal)")
    suspend fun clearEmptyDays()
}