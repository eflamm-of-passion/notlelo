package io.eflamm.notlelo.model;

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//@Entity
public class Picture(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uuid: String,
    val path: String
) {
    constructor(path: String): this(
        0,
        UUID.randomUUID().toString(),
        ""

    )
}
