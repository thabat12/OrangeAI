package com.example.orangeai.activities
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangeai.R
import com.example.orangeai.adapters.ExerciseHistoryAdapter
import com.example.orangeai.adapters.ForTodayExerciseAdapter
import com.example.orangeai.database.ExerciseDatabaseHandler
import com.example.orangeai.database.ExerciseHistoryDatabaseHandler
import com.example.orangeai.database.MainDatabaseHandler
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.ExerciseHistory
import com.example.orangeai.utils.PrefUtil
import com.example.orangeai.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_exercise.addFoodTab_move
import kotlinx.android.synthetic.main.activity_exercise.exercise_diet
import kotlinx.android.synthetic.main.activity_exercise.exercise_home
import kotlinx.android.synthetic.main.activity_exercise.exercise_profile
import java.util.*

open class ExerciseActivity : BaseActivity() , SensorEventListener {
    companion object {
        var ADD_EXERCISE_REQUEST_CODE = 1
        var EXTRA_EXERCISE_DETAILS = "extra_exercise_details"
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    private var sensorManager: SensorManager? = null

    var todoSize = 0
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)



        exercise_home.setOnClickListener {
            intent = Intent(this@ExerciseActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        exercise_diet.setOnClickListener {
            intent = Intent(this@ExerciseActivity, DietActivity::class.java)
            startActivity(intent)
            finish()
        }
        exercise_profile.setOnClickListener {
            intent = Intent(this@ExerciseActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        addFoodTab_move.setOnClickListener {
            intent = Intent(this@ExerciseActivity, AddFoodActivity::class.java)
            startActivity(intent)
            //finish()
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 1
        )
        see_exercises.setOnClickListener {
            intent = Intent(this@ExerciseActivity, ExerciseListActivity::class.java)
            startActivityForResult(intent, ADD_EXERCISE_REQUEST_CODE)

        }


        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) == PERMISSION_GRANTED
        ) {
            onResume()
            loadData()
            resetSteps()
        } else {

        }

//        loadData()
//        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "      Timer"
        getExerciseTodoListFromLocalDB()
        getExerciseHistoryFromLocalDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_EXERCISE_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getExerciseTodoListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back pressed")
            }
        }
    }



    fun getExerciseTodoListFromLocalDB(){
        val dbHandler = ExerciseDatabaseHandler(this)

        val getExerciseTodoList = dbHandler.getExercisesList()

        if (getExerciseTodoList.size > 0) {
            rv_for_today_exercises.visibility = View.VISIBLE
            pls_add_bithc.visibility = View.GONE
            setupExerciseTodoListRecyclerView(getExerciseTodoList)
        } else {
            rv_for_today_exercises.visibility = View.GONE
            pls_add_bithc.visibility = View.VISIBLE
        }
    }

    fun getExerciseHistoryFromLocalDB() {
        val dbHandler = ExerciseHistoryDatabaseHandler(this)
        val getHistory = dbHandler.getExerciseHistory()
        if (getHistory.size > 0) {
            setupHistoryExerciseList(getHistory)
            todoSize = getHistory.size
        } else {

        }
    }

    fun setupHistoryExerciseList(exerciseHistoryList: ArrayList<ExerciseHistory>) {
        rv_today_history.layoutManager = LinearLayoutManager(this)
        rv_today_history.setHasFixedSize(true)

        val historyAdapter = ExerciseHistoryAdapter(this, exerciseHistoryList)
        rv_today_history.adapter = historyAdapter

        historyAdapter.setOnClickListener(object :
            ExerciseHistoryAdapter.OnClickListener {
            override fun onClick(position: Int, model: ExerciseHistory) {
            }

        })

    }


    fun setupExerciseTodoListRecyclerView(exerciseTodoList: ArrayList<ActivityPrograms>) {
        rv_for_today_exercises.layoutManager = LinearLayoutManager(this)
        rv_for_today_exercises.setHasFixedSize(true)

        val placesAdapter = ForTodayExerciseAdapter(this, exerciseTodoList)
        rv_for_today_exercises.adapter = placesAdapter

        placesAdapter.setOnClickListener(object :
            ForTodayExerciseAdapter.OnClickListener {
            override fun onClick(position: Int, model: ActivityPrograms) {
                val intentMins = Intent(this@ExerciseActivity, PerformExerciseActivityMins::class.java)
                val intentReps = Intent(this@ExerciseActivity, PerformExerciseActivityReps::class.java)
                if (model.tag == 1) {
                    intentMins.putExtra(EXTRA_EXERCISE_DETAILS, model)
                    startActivity(intentMins)
                } else if (model.tag == 2) {
                    intentReps.putExtra(EXTRA_EXERCISE_DETAILS, model)
                    startActivity(intentReps)
                }
            }
        })

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_for_today_exercises.adapter as ForTodayExerciseAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getExerciseTodoListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_for_today_exercises)

    }



    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped

    private var secondsRemaining = 0L
    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {

        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(p0: SensorEvent?) {

        if (running) {
            totalSteps = p0!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            val dbHandler = MainDatabaseHandler(this)

            dbHandler.addCaloriesBurnMain(0)
            tv_stepsTaken.text = ("$currentSteps")
            var totalCaloriesBurned: Double = currentSteps * 0.04
            var totalMilesTraveled: Double = currentSteps * (1.0/2000)


            progress_circular.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }



            dbHandler.addStepsTaken(currentSteps, totalCaloriesBurned)
            val mainDBHandler = MainDatabaseHandler(this)
            val todayDetails = mainDBHandler.getTodayData()
            var totalProportion = ((todayDetails.caloriesBurned.toDouble() /
                    todayDetails.caloriesBurnGoal.toDouble()) * 100).toInt()
            progress_cal.progress = totalProportion

            var totalMetersTravelString = "%.1f".format(totalMilesTraveled) + " mi"
            var totalCaloriesBurnedString = todayDetails.caloriesBurned.toString()
            total_calories_burned.text = totalCaloriesBurnedString + " cal"
            progress_miles.progress = ((totalMilesTraveled / 5) * 100).toInt()
            progress_todo.progress = ( (todoSize.toDouble() / 5) * 100).toInt()
            tv_total_meters_travel.text = totalMetersTravelString
            todo_text.text = "$todoSize done"

        }
    }

    private fun resetSteps() {
        tv_stepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        tv_stepsTaken.setOnLongClickListener {
            previousTotalSteps = totalSteps
            tv_stepsTaken.text = 0.toString()
            saveData()

            true
        }

    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }





}



