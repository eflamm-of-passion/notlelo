package io.eflamm.notlelo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import io.eflamm.notlelo.model.*

@Database(entities = [Event::class, Picture::class, Product::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class NotleloDatabase: RoomDatabase() {
    // DAO
    abstract fun eventDao(): EventDao
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
                )
                    .addTypeConverter(DataConverter())
                    .build()
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