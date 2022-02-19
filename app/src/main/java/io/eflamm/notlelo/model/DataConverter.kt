package io.eflamm.notlelo.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@ProvidedTypeConverter
class DataConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let {  Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun stringToProducts(json: String?): List<Product> {
        if (json == null) {
            return emptyList()
        }
        val type = object : TypeToken<List<Product?>?>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun productsToString(products: List<Product>): String? {
        if(products == null) {
            return null
        }
        val type = object : TypeToken<List<Product?>?>() {}.type
        return gson.toJson(products, type)
    }

}