package io.eflamm.notlelo.repository

import androidx.annotation.WorkerThread
import io.eflamm.notlelo.database.*
import io.eflamm.notlelo.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EventRepository(
    private val eventDao: EventDao,
    private val dayDao: DayDao,
    private val mealDao: MealDao,
    private val productDao: ProductDao,
    private val pictureDao: PictureDao
    ) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    fun eventWithProducts(id: Long): Flow<EventWithDays> {
        return eventDao.getEventWithProducts(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertEvent(event: Event): Long {
        return eventDao.insert(event)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertFullProduct(eventId: Long, mealName: String, productName: String, picturePaths: List<String>): Long {
        val day = Day(eventId)
        val existingDay = dayDao.getDayByEventIdByDate(eventId, day.date).first()
        var dayId = 0L
        // if day already exists for the given event so we do not require to create another one
        if(existingDay == null) {
            dayId = dayDao.insert(day)
        } else {
            dayId = existingDay.id
        }

        val meal = Meal(dayId, mealName)
        var mealId = 0L
        // same thing for the meals
        val existingMeal = mealDao.getDayByEventIdByDate(dayId, mealName).first()
        if(existingMeal == null) {
            mealId = mealDao.insert(meal)
        } else {
            mealId = existingMeal.id
        }

        val product = Product(eventId, mealId, productName)
        val productId = productDao.insert(product)

        picturePaths.forEach { path ->
            val picture = Picture(productId, path)
            pictureDao.insert(picture)
        }
        return productId;
    }

    @WorkerThread
    suspend fun deleteEvents(events: List<Event>) {
        eventDao.deleteByUuids(events.map { it.uuid })
    }

    @WorkerThread
    suspend fun deleteProduct(product: Product) {
        productDao.deleteByUuid(product.uuid)

        productDao.clearEmptyProducts()
        mealDao.clearEmptyMeals()
        dayDao.clearEmptyDays()
    }

}