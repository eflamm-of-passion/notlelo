package io.eflamm.notlelo.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate

@ProvidedTypeConverter
class DataConverter {

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