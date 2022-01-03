package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Meal::class, parentColumns = arrayOf("id"), childColumns = arrayOf("dayId"))], indices = [Index("dayId")])
class Day {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    val uuid: UUID = UUID.randomUUID()
    val date: Date = Date()
    val meals: List<Meal> = emptyList()
}