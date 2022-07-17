package io.eflamm.notlelo.model

import androidx.room.Embedded
import androidx.room.Relation

class EventWithDays(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "event_id",
        entity = Day::class
    ) val days: List<DayWithMeals>
)
