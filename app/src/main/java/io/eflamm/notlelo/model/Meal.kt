package io.eflamm.notlelo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [ Index(value = [ "day_id", "name" ], unique = true) ])
class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "day_id")
    val dateId: Long,
    val uuid: UUID,
    @ColumnInfo(name = "name")
    val name: String
) {
    constructor(eventDateId: Long, name: String): this(
        0,
        eventDateId,
        UUID.randomUUID(),
        name
    )
}