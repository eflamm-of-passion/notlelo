package io.eflamm.notlelo.model;

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

//@Entity(foreignKeys = [ForeignKey(entity = Product::class, parentColumns = arrayOf("id"), childColumns = arrayOf("mealId"))], indices = [Index("mealId")])
class Meal(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val uuid: String,
    val name: String,
    val products: List<Product>
) {
    constructor(name: String): this(
        0,
        UUID.randomUUID().toString(),
        name,
        emptyList()
    )

}
