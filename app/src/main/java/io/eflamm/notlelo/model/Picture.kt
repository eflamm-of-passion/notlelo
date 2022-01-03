package io.eflamm.notlelo.model;

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
public class Picture {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    val uuid: UUID = UUID.randomUUID()
    val path: String = ""
}
