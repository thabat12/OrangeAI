package com.example.orangeai.models

import java.io.Serializable

data class TfValues(
    val label: String,
    val confidence: Float,
    val index: Int

) : Serializable