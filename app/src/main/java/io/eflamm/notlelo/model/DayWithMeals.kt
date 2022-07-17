package io.eflamm.notlelo.model

import androidx.room.Embedded
import androidx.room.Relation

class DayWithMeals(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "day_id",
        entity = Meal::class
    ) val meals: List<MealWithProducts>
)