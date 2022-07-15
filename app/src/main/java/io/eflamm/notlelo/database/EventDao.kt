package io.eflamm.notlelo.database

import androidx.room.*
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM Event")
    fun getAllEvents(): Flow<List<Event>>

    @Transaction
    @Query("SELECT * FROM Event WHERE id = :id")
    fun getEventWithProducts(id: Long): Flow<EventWithProducts>

    @Query("DELETE FROM Event WHERE name IN (:eventNames)")
    suspend fun deleteByNames(eventNames: List<String>)
}