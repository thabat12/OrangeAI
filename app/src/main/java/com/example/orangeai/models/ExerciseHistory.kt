package com.example.orangeai.models

import com.example.orangeai.activities.FinishExercise
import java.io.Serializable

data class ExerciseHistory(
    val id: Int,
    val exerciseName: String,
    val caloriesBurned: Double,
    val duration: String,
    val timeOfActivity: String
) : Serializable