package io.eflamm.notlelo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Picture::class, parentColumns = arrayOf("id"), childColumns = arrayOf("productId"))], indices = [Index("productId")])
public class Product {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    val uuid: UUID = UUID.randomUUID()
    val name: String = ""
    val pictures: List<Picture> = emptyList()
}
