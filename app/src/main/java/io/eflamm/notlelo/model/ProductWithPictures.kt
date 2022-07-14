package io.eflamm.notlelo.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithPictures (
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    ) val pictures: List<Picture>
)
