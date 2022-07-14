package io.eflamm.notlelo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.util.*

@Entity
class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    val uuid: String,
    val name: String,
    val date: LocalDate,
    val meal: String,
): Serializable {
    constructor(name: String, meal: String, eventId: Long): this(
        0,
        eventId,
        UUID.randomUUID().toString(),
        name,
        LocalDate.now(),
        meal
    )
}
