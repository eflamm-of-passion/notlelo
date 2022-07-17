package io.eflamm.notlelo.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.eflamm.notlelo.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// https://phauer.com/2018/best-practices-unit-testing-kotlin/#use-backticks-and-nested-inner-classes

@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var eventDao: EventDao
    private lateinit var dayDao: DayDao
    private lateinit var mealDao: MealDao
    private lateinit var productDao: ProductDao
    private lateinit var pictureDao: PictureDao
    private lateinit var database: NotleloDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NotleloDatabase::class.java)
            .addTypeConverter(DataConverter()
        ).build()
        eventDao = database.eventDao()
        dayDao = database.dateDao()
        mealDao = database.mealDao()
        productDao = database.productDao()
        pictureDao = database.pictureDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertEvent_eventWithNoProduct_eventCreatedWithName() = runTest {
        // given
        val expected = Event(name = "event name")

        // when
        eventDao.insert(expected)

        // then
        val actual: Event = eventDao.getAllEvents().first()[0]
        Assert.assertEquals(expected.name, actual.name)
        Assert.assertEquals(expected.uuid, actual.uuid)
    }

    @Test
    fun insertANewProduct_everyDatabaseLineCreated() = runTest {
        // given
        val eventToCreate = Event(name = "event name")

        // when
        val eventId = eventDao.insert(eventToCreate)

        val date = Day(eventId)
        val dateId = dayDao.insert(date)

        val meal = Meal(dateId, "meal name")
        val mealId = mealDao.insert(meal)

        val product = Product(eventId, mealId, "product name")
        val productId = productDao.insert(product)

        val picture1 = Picture(productId = productId, path = "some path")
        val picture2 = Picture(productId = productId, path = "some other path")
        pictureDao.insert(picture1)
        pictureDao.insert(picture2)
        val actualEventWithProduct = eventDao.getEventWithProducts(eventId).first()

        // then
        val numberOfPicturesExpected = 2
        Assert.assertEquals(numberOfPicturesExpected, actualEventWithProduct.days[0].meals[0].products[0].pictures.size)
        Assert.assertEquals("some path", actualEventWithProduct.days[0].meals[0].products[0].pictures[0].path)
        Assert.assertEquals("some other path", actualEventWithProduct.days[0].meals[0].products[0].pictures[1].path)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertTwoProductOnSameDay_throwException() = runTest {
        // given
        val eventToCreate = Event(name = "event name")

        // when
        val eventId = eventDao.insert(eventToCreate)

        val day1 = Day(eventId)
        val day1Id = dayDao.insert(day1)

        val meal1 = Meal(day1Id, "meal name 1")
        val meal1Id = mealDao.insert(meal1)

        val product1 = Product(eventId, meal1Id, "product name 1")
        val product1Id = productDao.insert(product1)

        val day2 = Day(eventId)
        val day2Id = dayDao.insert(day2)

        val meal2 = Meal(day2Id, "meal name 2")
        val meal2Id = mealDao.insert(meal2)

        val product2 = Product(eventId, meal2Id, "product name 2")
        val product2Id = productDao.insert(product2)
    }

}