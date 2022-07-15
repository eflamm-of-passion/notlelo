package io.eflamm.notlelo.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.eflamm.notlelo.model.DataConverter
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.Picture
import io.eflamm.notlelo.model.Product
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
    fun insertEventInsertProduct_eventAndProductReferencingEvent_eventUpdatedWithProducts() = runTest {
        // given
        val eventToCreate = Event(name = "event name")

        // when
        eventDao.insert(eventToCreate)
        val eventCreated = eventDao.getAllEvents().first()[0]
        val productToCreate = Product(name = "product name", meal = "meal name", eventId = eventCreated.id)
        productDao.insert(productToCreate)
        val actualEventWithProduct = eventDao.getEventWithProducts(eventCreated.id).first()

        // then
        val numberOfProductsExpected = 1
        Assert.assertEquals(eventToCreate.name, actualEventWithProduct.event.name)
        Assert.assertEquals(eventToCreate.uuid, actualEventWithProduct.event.uuid)
        Assert.assertEquals(numberOfProductsExpected, actualEventWithProduct.products.size)
        Assert.assertEquals(productToCreate.name, actualEventWithProduct.products[0].product.name)
        Assert.assertEquals(productToCreate.meal, actualEventWithProduct.products[0].product.meal)
        Assert.assertEquals(productToCreate.uuid, actualEventWithProduct.products[0].product.uuid)
    }

    @Test
    fun insertEventInsertProductInsertPictures_eventAndProductReferencingEventAndPicturesReferencingProduct_eventUpdatedWithProductsAndPictures() = runTest {
        // given
        val eventToCreate = Event(name = "event name")

        // when
        eventDao.insert(eventToCreate)
        val eventCreated = eventDao.getAllEvents().first()[0]
        val productToCreate = Product(name = "product name", meal = "meal name", eventId = eventCreated.id)
        val productCreatedId = productDao.insert(productToCreate)
        val picture1 = Picture(productId = productCreatedId, path = "some path")
        val picture2 = Picture(productId = productCreatedId, path = "some other path")
        pictureDao.insert(picture1)
        pictureDao.insert(picture2)
        val actualEventWithProduct = eventDao.getEventWithProducts(eventCreated.id).first()

        // then
        val numberOfPicturesExpected = 2
        Assert.assertEquals(numberOfPicturesExpected, actualEventWithProduct.products[0].pictures.size)
        Assert.assertEquals("some path", actualEventWithProduct.products[0].pictures[0].path)
        Assert.assertEquals("some other path", actualEventWithProduct.products[0].pictures[1].path)
    }
}