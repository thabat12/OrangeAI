package com.example.orangeai.models

import java.io.Serializable

data class Today(
    val index: Int,
    val currentDay: Int,
    val program: String,
    val stepsTaken: String,
    val caloriesBurned: Int,
    val caloriesBurnGoal: Int,
    val caloriesGained: Int,
    val caloriesGainGoal: Int,
    val waterIntake: Int,
    val exerciseAward: Int,
    val dietAward: Int,
    val date: String

) : Serializable