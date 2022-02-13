package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

//@Entity(foreignKeys = [ForeignKey(entity = Picture::class, parentColumns = arrayOf("id"), childColumns = arrayOf("productId"))], indices = [Index("productId")])
@Entity
public class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    val name: String,
    val date: Date,
    val meal: String,
    //val pictures: List<Picture>
) {
    constructor(name: String, meal: String): this(
        0,
        UUID.randomUUID().toString(),
        name,
        Date(),
        meal,
        //emptyList()
    )
}
