package com.example.orangeai.models

import java.io.Serializable

data class Recipes(
    val id: Int,
    val title: String,
    val description:String,
    val calories: Int,
    val img: Int,
    val web: String

) : Serializable