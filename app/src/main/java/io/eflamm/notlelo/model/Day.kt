package io.eflamm.notlelo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity(indices = [ Index(value = [ "event_id", "date" ], unique = true) ])
class Day(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    val uuid: UUID,
    @ColumnInfo(name = "date")
    val date: LocalDate,
) {
    constructor(eventId: Long): this(
        0,
        eventId,
        UUID.randomUUID(),
        LocalDate.now()
    )

}