package io.eflamm.notlelo.repository

import androidx.annotation.WorkerThread
import io.eflamm.notlelo.database.EventDao
import io.eflamm.notlelo.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(event: Event) {
        eventDao.insert(event)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeByNames(eventNames: List<String>) {
        eventDao.deleteByNames(eventNames)
    }
}