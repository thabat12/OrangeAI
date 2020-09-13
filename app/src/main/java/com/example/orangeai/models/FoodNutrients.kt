package com.example.orangeai.models

import java.io.Serializable

data class FoodNutrients(
    val foodName: String,
    val proteinName: String,
    val proteinValue: Double,
    val proteinUnits: String,
    val lipidName: String,
    val lipidValue: Double,
    val lipidUnits: String,
    val carbName: String,
    val carbValue: Double,
    val carbUnits: String,
    val calories: Int,
    val date: String?,
    val imageURI: String?
) : Serializable