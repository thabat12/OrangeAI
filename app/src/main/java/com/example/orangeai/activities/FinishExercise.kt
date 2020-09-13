package com.example.orangeai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.orangeai.R
import com.example.orangeai.database.DietDatabaseHandler
import com.example.orangeai.database.ExerciseHistoryDatabaseHandler
import com.example.orangeai.database.MainDatabaseHandler
import com.example.orangeai.models.ExerciseHistory
import kotlinx.android.synthetic.main.activity_finish_exercise.*

class FinishExercise : AppCompatActivity() {

    var exerciseHistoryModel: ExerciseHistory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_exercise)

        if (intent.hasExtra(PerformExerciseActivityMins.EXERCISE_DETAILS)) {
            exerciseHistoryModel =
                intent.getSerializableExtra(PerformExerciseActivityMins.EXERCISE_DETAILS) as ExerciseHistory
        } else if (intent.hasExtra(PerformExerciseActivityReps.EXERCISE_DETAILS_PP)) {
            exerciseHistoryModel =
                intent.getSerializableExtra(PerformExerciseActivityReps.EXERCISE_DETAILS_PP) as ExerciseHistory
        }

        var exerciseSummary = ""
        exerciseSummary += exerciseHistoryModel!!.exerciseName + "\n" + "duration: " + exerciseHistoryModel!!.duration + "\n" +
                "calories burned: " + exerciseHistoryModel!!.caloriesBurned + "\n" + "time of activity: " + exerciseHistoryModel!!.timeOfActivity

        show_exercise_summary.text = exerciseSummary

        return_button.setOnClickListener {

                val dbHandler = ExerciseHistoryDatabaseHandler(this)
                val addExercise = dbHandler.addExerciseToHistory(exerciseHistoryModel!!)

            val coolkid = MainDatabaseHandler(this)
            coolkid.addCaloriesBurnMain(exerciseHistoryModel!!.caloriesBurned.toInt())
            startActivity(Intent(this@FinishExercise, ExerciseActivity::class.java))
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            finish()


        }
    }

}