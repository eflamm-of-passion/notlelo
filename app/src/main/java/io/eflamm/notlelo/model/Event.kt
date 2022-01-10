package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

//@Entity(foreignKeys = [ForeignKey(entity = Day::class, parentColumns = arrayOf("id"), childColumns = arrayOf("eventId"))], indices = [Index("eventId")])
@Entity
class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    var name: String,
//    val days: List<Day>,
) {
    constructor(name: String) : this(
            0,
            UUID.randomUUID().toString(),
            name,
//            emptyList()
    )
}
