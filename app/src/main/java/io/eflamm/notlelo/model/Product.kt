package io.eflamm.notlelo.model

import androidx.room.*
import java.util.*

@Entity
public class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    val uuid: String,
    val name: String,
    val date: Date,
    val meal: String,
    //val pictures: List<Picture>
) {
    constructor(name: String, meal: String, eventId: Long): this(
        0,
        eventId,
        UUID.randomUUID().toString(),
        name,
        Date(),
        meal,
        //emptyList()
    )
}
