package io.eflamm.notlelo.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import io.eflamm.notlelo.model.Event

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createEvent(event: Event)

    @Update
    fun updateEvent(event: Event)

    @Delete
    fun deleteEvent(event: Event)

    @Query("SELECT * FROM Event where id = :eventId")
    fun getEvent(eventId: Long): LiveData<Event>
}