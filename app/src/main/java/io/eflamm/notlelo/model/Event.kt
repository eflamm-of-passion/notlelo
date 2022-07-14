package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    var name: String,
): Serializable {
    constructor(name: String) : this(
            0,
            UUID.randomUUID().toString(),
            name
    )
}
