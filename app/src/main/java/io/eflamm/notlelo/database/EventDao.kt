package io.eflamm.notlelo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.eflamm.notlelo.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM Event")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM Event WHERE id = :eventId")
    fun getEvent(eventId: Long): Flow<Event>

    @Query("DELETE FROM Event WHERE name IN (:eventNames)")
    suspend fun deleteByNames(eventNames: List<String>)
}