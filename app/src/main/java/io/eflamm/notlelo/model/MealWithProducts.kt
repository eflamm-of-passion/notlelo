package io.eflamm.notlelo.model

import androidx.room.Embedded
import androidx.room.Relation

class MealWithProducts(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "id",
        entityColumn = "meal_id",
        entity = Product::class
    ) val products: List<ProductWithPictures>
)