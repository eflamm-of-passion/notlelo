package io.eflamm.notlelo.model

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithProducts(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "event_id",
        entity = Product::class
    ) val products: List<ProductWithPictures>,
)