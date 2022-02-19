package io.eflamm.notlelo.model

import java.time.LocalDate

class StructuredEvent(
    var days: MutableMap<LocalDate, LightMeal>
)

class LightDay(
    var date: LocalDate,
    var meals: MutableMap<String, LightMeal>
)

class LightMeal(
    var name: String,
    var products: MutableMap<String, LightProduct>
)

class LightProduct(
    var id: Long,
    var name: String,
    var pictures: MutableList<String> = mutableListOf()
)

