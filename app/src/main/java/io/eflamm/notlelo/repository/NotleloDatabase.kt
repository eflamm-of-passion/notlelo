package io.eflamm.notlelo.repository

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.eflamm.notlelo.model.*

@Database(entities = [Day::class, Event::class, Meal::class, Picture::class, Product::class], version = 1, exportSchema = false)
abstract class NotleloDatabase: RoomDatabase() {
    // DAO
    abstract fun dayDao(): DayDao
    abstract fun eventDao(): EventDao
    abstract fun mealDao(): MealDao
    abstract fun productDao(): ProductDao
    abstract fun pictureDao(): PictureDao

    companion object {
        // singleton
        @Volatile
        private var INSTANCE: NotleloDatabase? = null

        // instance
        fun getInstance(context: Context): NotleloDatabase? {
            if(INSTANCE == null) {
                synchronized(NotleloDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, NotleloDatabase::class.java, "NotleloDatabase.db")
                        .addCallback(prepopulateDatabase())
                        .build()
                }
            }
            return INSTANCE
        }

        private fun prepopulateDatabase(): RoomDatabase.Callback {
            return object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // TODO populate
                }
            }
        }
    }
}