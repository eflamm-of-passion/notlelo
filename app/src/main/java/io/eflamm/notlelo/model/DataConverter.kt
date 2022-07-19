package io.eflamm.notlelo.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate

@ProvidedTypeConverter
class DataConverter {
//    @TypeConverter
//    fun fromTimestamp(value: Long?): LocalDate? {
//        return value?.let {  Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: LocalDate?): Long? {
//        return date?.toEpochDay()
//    }

    @TypeConverter
    fun toDate(dateString: String?): LocalDate? {
        return if (dateString == null) {
            null
        } else {
            LocalDate.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDate?): String? {
        return if (date == null) {
            null
        } else {
            date.toString()
        }
    }

}