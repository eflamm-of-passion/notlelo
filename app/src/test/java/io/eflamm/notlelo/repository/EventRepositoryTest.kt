package io.eflamm.notlelo.repository

import io.eflamm.notlelo.database.*
import io.eflamm.notlelo.model.Day
import io.eflamm.notlelo.model.Meal
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*

@RunWith(JUnit4::class)
class EventRepositoryTest {

    private lateinit var eventDao: EventDao
    private lateinit var dayDao: DayDao
    private lateinit var mealDao: MealDao
    private lateinit var productDao: ProductDao
    private lateinit var pictureDao: PictureDao
    private lateinit var eventRepository: EventRepository

    @Before
    fun setup() {
        eventDao = mock {  }
        dayDao = mock {  }
        mealDao = mock {  }
        productDao = mock {  }
        pictureDao = mock {  }
        eventRepository = EventRepository(eventDao, dayDao, mealDao, productDao, pictureDao)
    }

    @Test
    fun insertFullProduct_noExistingData_everyDaoIsCalled() {
        runBlocking {
            // given
            var eventId = 1L
            var mealName = "meal name"
            var productName = "product name"
            var picturePaths = listOf("some path", "some other path")

            val dayFlow = flow<Day?> {
                emit(null)
            }
            val mealFlow = flow<Meal?> {
                emit(null)
            }

            `when`(dayDao.getDayByEventIdByDate(anyLong(), any())).thenReturn(dayFlow)
            `when`(mealDao.getDayByEventIdByDate(any(), anyString())).thenReturn(mealFlow)

            `when`(dayDao.insert(any())).thenReturn(2L)
            `when`(mealDao.insert(any())).thenReturn(3L)
            `when`(productDao.insert(any())).thenReturn(4L)

            //when
            eventRepository.insertFullProduct(eventId, mealName, productName, picturePaths)

            // then
            verify(dayDao).insert(any())
            verify(mealDao).insert(any())
            verify(productDao).insert(any())
            verify(pictureDao, times(2)).insert(any())
        }
    }

    @Test
    fun insertFullProduct_alreadyExistingDay_dayDaoNotCalled() {
        runBlocking {
            // given
            var eventId = 1L
            var mealName = "meal name"
            var productName = "product name"
            var picturePaths = listOf("some path", "some other path")
            val alreadyExistingDay = Day(eventId)

            val dayFlow = flow<Day?> {
                emit(alreadyExistingDay)
            }
            val mealFlow = flow<Meal?> {
                emit(null)
            }

            `when`(dayDao.getDayByEventIdByDate(anyLong(), any())).thenReturn(dayFlow)
            `when`(mealDao.getDayByEventIdByDate(any(), anyString())).thenReturn(mealFlow)

            `when`(dayDao.insert(any())).thenReturn(2L)
            `when`(mealDao.insert(any())).thenReturn(3L)
            `when`(productDao.insert(any())).thenReturn(4L)

            //when
            eventRepository.insertFullProduct(eventId, mealName, productName, picturePaths)

            // then
            verify(dayDao, never()).insert(any())
            verify(mealDao).insert(any())
            verify(productDao).insert(any())
            verify(pictureDao, times(2)).insert(any())
        }
    }

    @Test
    fun insertFullProduct_alreadyExistingMeal_mealDaoNotCalled() {
        runBlocking {
            // given
            var eventId = 1L
            var mealName = "meal name"
            var productName = "product name"
            var picturePaths = listOf("some path", "some other path")
            val alreadyExistingMeal = Meal(2L, "meal name")

            val dayFlow = flow<Day?> {
                emit(null)
            }
            val mealFlow = flow<Meal?> {
                emit(alreadyExistingMeal)
            }

            `when`(dayDao.getDayByEventIdByDate(anyLong(), any())).thenReturn(dayFlow)
            `when`(mealDao.getDayByEventIdByDate(any(), anyString())).thenReturn(mealFlow)

            `when`(dayDao.insert(any())).thenReturn(2L)
            `when`(mealDao.insert(any())).thenReturn(3L)
            `when`(productDao.insert(any())).thenReturn(4L)

            //when
            eventRepository.insertFullProduct(eventId, mealName, productName, picturePaths)

            // then
            verify(dayDao).insert(any())
            verify(mealDao, never()).insert(any())
            verify(productDao).insert(any())
            verify(pictureDao, times(2)).insert(any())
        }
    }

    @Test
    fun insertFullProduct_noPictures_pictureDaoNotCalled() {
        runBlocking {
            // given
            var eventId = 1L
            var mealName = "meal name"
            var productName = "product name"
            var picturePaths: List<String> = emptyList()

            val dayFlow = flow<Day?> {
                emit(null)
            }
            val mealFlow = flow<Meal?> {
                emit(null)
            }

            `when`(dayDao.getDayByEventIdByDate(anyLong(), any())).thenReturn(dayFlow)
            `when`(mealDao.getDayByEventIdByDate(any(), anyString())).thenReturn(mealFlow)

            `when`(dayDao.insert(any())).thenReturn(2L)
            `when`(mealDao.insert(any())).thenReturn(3L)
            `when`(productDao.insert(any())).thenReturn(4L)

            //when
            eventRepository.insertFullProduct(eventId, mealName, productName, picturePaths)

            // then
            verify(dayDao).insert(any())
            verify(mealDao).insert(any())
            verify(productDao).insert(any())
            verify(pictureDao, never()).insert(any())
        }
    }
}