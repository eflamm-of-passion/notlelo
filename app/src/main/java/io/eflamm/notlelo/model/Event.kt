package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

//@Entity(foreignKeys = [ForeignKey(entity = Product::class, parentColumns = arrayOf("id"), childColumns = arrayOf("eventId"))], indices = [Index("eventId")])
@Entity
class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    var name: String,
    var products: List<Product>
): Serializable {
    constructor(name: String) : this(
            0,
            UUID.randomUUID().toString(),
            name,
            emptyList()
    )
}
