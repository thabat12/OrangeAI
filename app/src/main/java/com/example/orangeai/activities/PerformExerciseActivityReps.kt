package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.camera2.TotalCaptureResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.orangeai.R
import com.example.orangeai.activities.PerformExerciseActivityMins.Companion.EXERCISE_DETAILS
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.ExerciseHistory
import com.example.orangeai.models.User
import com.projemanag.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_perform_exercise_mins.*
import kotlinx.android.synthetic.main.activity_perform_exercise_mins.exercise_title_perform
import kotlinx.android.synthetic.main.activity_perform_exercise_mins.fab_start
import kotlinx.android.synthetic.main.activity_perform_exercise_reps.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class PerformExerciseActivityReps : AppCompatActivity() {

    companion object {
        var EXERCISE_DETAILS_PP = "exercise_details"
    }


    var exerciseDetailModel: ActivityPrograms? = null
    var timeForExerciseMillis: Long = 0
    var numberOfReps: Int = 0
    var timeInMins: Int = 0
    var timeInSecs: Int = 1
    var calPerRep: Double = 0.0
    var totalCaloriesToBeBurned: Double = 0.0
    var repsPerMinAvg: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform_exercise_reps)


        card_visible_one.visibility = View.VISIBLE
        card_visible_two.visibility = View.VISIBLE
        fab_start.visibility = View.VISIBLE
        text_visible.visibility = View.VISIBLE
        tv_artificial_timer.visibility = View.GONE
        imageView26.visibility = View.GONE
        total_caloriesToBeBurned.visibility = View.GONE


        if (intent.hasExtra(ExerciseActivity.EXTRA_EXERCISE_DETAILS)) {
            exerciseDetailModel =
                intent.getSerializableExtra(ExerciseActivity.EXTRA_EXERCISE_DETAILS) as ActivityPrograms
        }
        FirestoreClass().loadUserData(this)
        if (exerciseDetailModel != null) {
            val title: String = exerciseDetailModel!!.title
            exercise_title_perform.text = title
        }


        fab_start.setOnClickListener {
            timeForExerciseMillis = 1000 * 60 * (edit_text_mins.text.toString()).toLong()
            numberOfReps = edit_text_reps.text.toString().toInt()
            timeInMins = (edit_text_mins.text.toString()).toInt()
            totalCaloriesToBeBurned = numberOfReps * calPerRep
            repsPerMinAvg = numberOfReps / (timeInMins.toDouble())
            reps_per_min_avg.text  = "Reps per minute: " + "%.1f".format(repsPerMinAvg)


            card_visible_one.visibility = View.GONE
            card_visible_two.visibility = View.GONE
            fab_start.visibility = View.GONE
            text_visible.visibility = View.GONE
            tv_artificial_timer.visibility = View.VISIBLE
            imageView26.visibility = View.VISIBLE
            total_caloriesToBeBurned.visibility = View.VISIBLE
            total_caloriesToBeBurned.text = "Total Calories: "+ "%.1f".format(totalCaloriesToBeBurned)

            startExercise()
        }



    }
    fun customizeCaloriesReps(loggedInUser: User) {
        if (loggedInUser.weight <= 135) {
            calPerRep = exerciseDetailModel!!.calPerMinL
        } else if (loggedInUser.weight in 136..162) {
            calPerRep = exerciseDetailModel!!.calPerMinM
        } else {
            calPerRep = exerciseDetailModel!!.calPerMinH
        }
    }

    fun startExercise() {

        val timer = object: CountDownTimer(timeForExerciseMillis, 1000 ) {

            var elapsedTimeChecker = 0

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {


                if (timeInSecs == 0) {
                    timeInSecs = 60
                    timeInMins -= 1
                }
                if (timeInSecs < 10) {

                }

                timeInSecs -= 1
                if (timeInSecs < 10) {
                    tv_artificial_timer.text = "$timeInMins:0$timeInSecs"
                } else {
                    tv_artificial_timer.text = "$timeInMins:$timeInSecs"
                }


            }
            override fun onFinish() {
                tv_artificial_timer.text = "0:00"

                val duration: String = edit_text_mins.text.toString() + ":00"
                val exerciseName: String = exerciseDetailModel!!.title

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())

                val caloriesBurned = totalCaloriesToBeBurned


                val model = ExerciseHistory(0, exerciseName, caloriesBurned, duration, currentDate)

                intent = Intent(this@PerformExerciseActivityReps, FinishExercise::class.java)
                intent.putExtra(EXERCISE_DETAILS_PP, model)
                startActivity(intent)

            }


        }.start()
    }

}