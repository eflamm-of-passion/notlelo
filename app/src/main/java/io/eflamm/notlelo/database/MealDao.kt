package io.eflamm.notlelo.database

import androidx.room.*
import io.eflamm.notlelo.model.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(meal: Meal): Long

    @Transaction
    @Query("SELECT * FROM Meal WHERE day_id = :dayId AND name = :name")
    fun getDayByEventIdByDate(dayId: Long, name: String): Flow<Meal?>

    @Query("DELETE FROM Meal WHERE id NOT IN (SELECT DISTINCT meal_id FROM Product)")
    suspend fun clearEmptyMeals()
}