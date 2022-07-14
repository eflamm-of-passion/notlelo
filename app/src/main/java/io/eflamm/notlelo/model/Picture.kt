package io.eflamm.notlelo.model;

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
class Picture(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    @ColumnInfo(name = "product_id")
    val productId: Long,
    val path: String
): Serializable {
    constructor(productId: Long, path: String): this(
        0,
        UUID.randomUUID().toString(),
        productId,
        path

    )
}
