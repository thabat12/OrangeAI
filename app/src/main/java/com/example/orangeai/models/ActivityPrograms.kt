package com.example.orangeai.models

import java.io.Serializable

data class ActivityPrograms(
    val id: Int,
    val tag: Int,
    val title: String,
    val image: String,
    val calPerMinL: Double,
    val calPerMinM: Double,
    val calPerMinH: Double

    ) : Serializable