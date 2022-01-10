package io.eflamm.notlelo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.eflamm.notlelo.model.*

//@Database(entities = [Day::class, Event::class, Meal::class, Picture::class, Product::class], version = 1, exportSchema = false)
@Database(entities = [Event::class], version = 1, exportSchema = false)
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
        fun getInstance(context: Context): NotleloDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotleloDatabase::class.java,
                    "notlelo_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
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