package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Day::class, parentColumns = arrayOf("id"), childColumns = arrayOf("eventId"))], indices = [Index("eventId")])
public class Event {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    val uuid: UUID = UUID.randomUUID()
    val name: String = ""
    val days: List<Day> = emptyList()
}
