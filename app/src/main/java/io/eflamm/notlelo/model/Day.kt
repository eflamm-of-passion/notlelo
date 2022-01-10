package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

//@Entity(foreignKeys = [ForeignKey(entity = Meal::class, parentColumns = arrayOf("id"), childColumns = arrayOf("dayId"))], indices = [Index("dayId")])
class Day(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    val date: Long,
    val meals: List<Meal>
) {

    constructor() : this(
        0,
        UUID.randomUUID().toString(),
        Date().time,
        emptyList()

    )
}