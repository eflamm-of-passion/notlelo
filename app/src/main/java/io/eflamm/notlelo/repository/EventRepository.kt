package io.eflamm.notlelo.repository

import androidx.annotation.WorkerThread
import io.eflamm.notlelo.database.EventDao
import io.eflamm.notlelo.database.PictureDao
import io.eflamm.notlelo.database.ProductDao
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithProducts
import io.eflamm.notlelo.model.Picture
import io.eflamm.notlelo.model.Product
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao, private val productDao: ProductDao, private val pictureDao: PictureDao) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    fun eventWithProducts(id: Long): Flow<EventWithProducts> {
        return eventDao.getEventWithProducts(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertEvent(event: Event): Long {
        return eventDao.insert(event)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeByNames(eventNames: List<String>) {
        eventDao.deleteByNames(eventNames)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertProductWithPictures(product: Product, pictures: List<Picture>): Long {
        val productCreatedId = productDao.insert(product)
        pictures.forEach { picture ->
            picture.productId = productCreatedId
            pictureDao.insert(picture)
        }
        return productCreatedId;
    }

}