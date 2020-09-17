package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.orangeai.R
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


class PerformExerciseActivityMins : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    companion object {
        var EXERCISE_DETAILS = "exercise_details"
    }

    var timerRunning: Boolean = false
    var elapsedTime: Int = 0
    var calPerMin: Double = 0.0
    var exerciseDetailModel: ActivityPrograms? = null

//    var timer = object: CountDownTimer(3600000, 1000)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform_exercise_mins)

        if (intent.hasExtra(ExerciseActivity.EXTRA_EXERCISE_DETAILS)) {
            exerciseDetailModel =
                intent.getSerializableExtra(ExerciseActivity.EXTRA_EXERCISE_DETAILS) as ActivityPrograms
        }
        FirestoreClass().loadUserData(this)
        animationView3.visibility = View.INVISIBLE
        animationView3Stop.visibility = View.VISIBLE
        if (exerciseDetailModel != null) {
            val title: String = exerciseDetailModel!!.title
            exercise_title_perform.text = title
        }




        // initialize the countdowntimer
        val timer = object: CountDownTimer(3600000, 1000 ) {
            @SuppressLint("SetTextI18n")
            var totalCalories: Double = 0.0



            override fun onTick(millisUntilFinished: Long) {
                elapsedTime += 1000
//                if (timerRunning == false){
//                    elapsedTime %= 5
//                }
                if (elapsedTime % 5000 == 0) {
                    totalCalories += (calPerMin!! / 12 )
                }


                elapsed_time_check.text = "%.1f".format(totalCalories)
            }
            override fun onFinish() {

            }


        }


        var stopTime:Long = 0

        fab_start.setOnClickListener {
            view_timer.base = SystemClock.elapsedRealtime() + stopTime
            fab_start.setVisibility(View.GONE)
            fab_pause.visibility = View.VISIBLE
            fab_stop.visibility = View.VISIBLE
            view_timer.start()
            timer.start()
            timerRunning = true
            animationView3Stop.visibility = View.INVISIBLE
            animationView3.visibility = View.VISIBLE
        }
        fab_pause.setOnClickListener {

            if (timerRunning == true) {
                // var timePassed = SystemClock.elapsedRealtime()
                stopTime = view_timer.base - SystemClock.elapsedRealtime()
                view_timer.stop()
                timerRunning = false
                fab_pause.text = "Resume Exercise"
                elapsedTime %= 5000
                timer.cancel()
                animationView3Stop.visibility = View.VISIBLE
                animationView3.visibility = View.INVISIBLE

            } else {
                view_timer.base = SystemClock.elapsedRealtime() + stopTime
                fab_pause.text = "Take A Break"
                view_timer.start()
                timerRunning = true
                timer.start()
                animationView3.visibility = View.VISIBLE
                animationView3Stop.visibility = View.INVISIBLE
            }

        }
        fab_stop.setOnClickListener {
            view_timer.base = SystemClock.elapsedRealtime()
            stopTime = 0
            view_timer.stop()


            val duration: String = view_timer.text.toString()
            val exerciseName: String = exerciseDetailModel!!.title

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())

            val caloriesBurned = elapsed_time_check.text.toString().toDouble()


            val model = ExerciseHistory(exerciseName, caloriesBurned, duration, currentDate)

            intent = Intent(this@PerformExerciseActivityMins, FinishExercise::class.java)
            intent.putExtra(PerformExerciseActivityMins.EXERCISE_DETAILS, model)
            startActivity(intent)


        }
        var string = view_timer.text



    }
    fun customizeCalories(loggedInUser: User) {
        if (loggedInUser.weight <= 135) {
            calPerMin = exerciseDetailModel!!.calPerMinL
        } else if (loggedInUser.weight in 136..162) {
            calPerMin = exerciseDetailModel!!.calPerMinM
        } else {
            calPerMin = exerciseDetailModel!!.calPerMinH
        }


    }

//    fun startStopwatch(exerciseDetailModel: ActivityPrograms?) {
//        elapsedTime = 0
//        var calPerMin = exerciseDetailModel?.calPerMin
//        var totalCalories: Double = 0.0
//        val timer = object: CountDownTimer(3600000, 1000 ) {
//            @SuppressLint("SetTextI18n")
//
//            override fun onTick(millisUntilFinished: Long) {
//                if (timerRunning == false){
//                    elapsedTime = 0
//                }
//                elapsedTime += 1000
//                if (elapsedTime % 5000 == 0) {
//                    totalCalories += (calPerMin!! / 12 )
//                    if (timerRunning == false) {
//                        totalCalories -= (calPerMin!! / 12 )
//                    }
//                }
//
//
//                elapsed_time_check.text = "%.1f".format(totalCalories)
//            }
//            override fun onFinish() {
//
//            }
//
//
//        }.start()
//    }
//
//    @SuppressLint("SetTextI18n")
//    fun stoppingStopwatch() {
//
//        timerState = StopWatchState.Stopped
//        millis = 0
//        seconds = 0
//        minutes = 0
//        tv_stopwatch.text = "00:00.00"
//    }
}






